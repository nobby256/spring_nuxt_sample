package com.example.demo.library.security.configurer.standard;

import java.io.IOException;
import java.util.Collections;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.LoginUrlAuthenticationEntryPoint;
import org.springframework.security.web.util.matcher.AndRequestMatcher;
import org.springframework.security.web.util.matcher.MediaTypeRequestMatcher;
import org.springframework.security.web.util.matcher.NegatedRequestMatcher;
import org.springframework.security.web.util.matcher.RequestHeaderRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.web.util.WebUtils;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * 認証エラー発生時に「ログイン画面へリダイレクトすべきか」それとも
 * 「セッションタイムアウト扱いで 401 を返すべきか」を判定するための
 * AuthenticationEntryPoint 実装です。
 * <p>
 * 本クラスは、リクエスト内容とセッション状態から次の 2 パターンを判別します。
 * </p>
 * <ul>
 * <li><b>ログイン画面へリダイレクトすべきケース</b>
 * <ul>
 * <li>ブラウザからの通常の HTML GET リクエストであり（Accept: text/html, 非AJAX）</li>
 * <li>かつ次のいずれかを満たす場合
 * <ul>
 * <li>セッション ID が送信されていない（ブラウザ起動直後の初回アクセスとみなす）</li>
 * <li>セッション ID が送信されているが無効であり、かつリクエスト URL が
 * ブックマークを許容した入口パス（bookmarkAwareEntryPointMatcher）にマッチする
 * （タブ閉じ → セッションタイムアウト → ブックマークから再開、など）</li>
 * </ul>
 * </li>
 * </ul>
 * </li>
 * <li><b>セッションタイムアウト／未認証として 401 を返すケース</b>
 * <ul>
 * <li>上記以外のすべてのリクエスト
 * （入口パス以外の URL、HTML 以外、AJAX、POST など）</li>
 * </ul>
 * </li>
 * </ul>
 * <p>
 * ログイン画面へのリダイレクトには {@link LoginUrlAuthenticationEntryPoint} の既定挙動
 * （コンストラクタで指定された loginFormUrl へのリダイレクト）を利用します。
 * 401 応答時には、具体的なレスポンスボディ（HTML エラーページ、JSON など）の生成は
 * Spring Boot の ErrorController やカスタム ErrorViewResolver に委ねます。
 * これにより、SPA／REST／AJAX では一貫して 401 を受け取りクライアント側で
 * 「タイムアウト／未ログイン」の処理を実装でき、動的 Web 画面では
 * 「ブラウザ起動直後／タブ閉じ後の再開」と「利用中タイムアウト」の UX を分けて制御できます。
 * </p>
 * <p>
 * ブックマークを許容する入口パス（初手アクセス想定 URL 群）は、
 * コンストラクタ引数 {@code bookmarkAwareEntryPointMatcher} で注入します。
 * アプリケーション側で「どの URL を利用開始地点と見なすか」を柔軟に定義できます。
 * </p>
 */
public class LoginOrTimeoutAuthenticationEntryPoint extends LoginUrlAuthenticationEntryPoint {

    /** {@link Logger}。 */
    private static final Logger logger = LoggerFactory.getLogger(LoginOrTimeoutAuthenticationEntryPoint.class);

    /** ブラウザからのGET要求を表すMatcher。 */
    private final RequestMatcher browserHtmlGetRequestMatcher;

    /** ブックマークを許容するURLへのリクエストであることを判定するMatcher。 */
    private final RequestMatcher bookmarkAwareEntryPointMatcher;

    /**
     * コンストラクタ。
     * 
     * @param bookmarkAwareEntryPointMatcher ブックマークを許容するURLへのリクエストであることを判定するMatcher
     * @param loginFormUrl                   ログイン画面のURL
     */
    public LoginOrTimeoutAuthenticationEntryPoint(
            RequestMatcher bookmarkAwareEntryPointMatcher,
            String loginFormUrl) {
        super(loginFormUrl);
        this.browserHtmlGetRequestMatcher = getBrowserHtmlGetRequestMatcher();
        this.bookmarkAwareEntryPointMatcher = bookmarkAwareEntryPointMatcher;
    }

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response,
            AuthenticationException authException) throws IOException, ServletException {

        if (browserHtmlGetRequestMatcher.matches(request)) { // ブラウザからのGETリクエストか？
            if (request.getRequestedSessionId() == null) { // セッションIDを受信してない？
                // ブラウザ起動 → 初回アクセスと判断
                super.commence(request, response, authException); // ログイン画面へのリダイレクト
                return;
            } else if (!request.isRequestedSessionIdValid()) { // セッションIDを受信、かつ、IDが無効？
                if (bookmarkAwareEntryPointMatcher.matches(request)) { // ブックマーク想定URLか？
                    // タブ閉じ → （タイムアウト） → ブックマークから利用開始と判断
                    super.commence(request, response, authException); // ログイン画面へのリダイレクト
                    return;
                }
            }
        }

        // HTML系の要求とREST系のレスポンスの違いはErrorControllerに任せる。
        // HTMLフラグメントを返すAJAXのレスポンスはカスタムのErrorViewResolver内で対応を想定。
        logger.debug("Responding with 401 status code");
        response.sendError(HttpStatus.UNAUTHORIZED.value());
        // ErrorControllerやErrorViewResolverで例外クラスを取得できるようにする為に例外を格納
        request.setAttribute(WebUtils.ERROR_EXCEPTION_ATTRIBUTE, authException);
    }

    /**
     * ブラウザからのGET要求を表すMatcher。
     * 
     * @return text/html かつ 非AJAX かつ GET
     */
    private RequestMatcher getBrowserHtmlGetRequestMatcher() {
        MediaTypeRequestMatcher htmlMatcher = new MediaTypeRequestMatcher(MediaType.TEXT_HTML);
        htmlMatcher.setIgnoredMediaTypes(Collections.singleton(MediaType.ALL)); // */* は許容しない
        NegatedRequestMatcher notAjacMatcher = new NegatedRequestMatcher(
                new RequestHeaderRequestMatcher("X-Requested-With", "XMLHttpRequest"));
        RequestMatcher getMatcher = (HttpServletRequest request) -> request.getMethod().toUpperCase().equals("GET");
        return new AndRequestMatcher(htmlMatcher, notAjacMatcher, getMatcher);
    }

}
