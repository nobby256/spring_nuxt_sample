package com.example.demo.library.security;

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
 * 「セッションタイムアウト扱いで 401 を返すべきか」を判定するための AuthenticationEntryPoint 実装です。
 * <p>
 * 次の条件をすべて満たすリクエストを「ブラウザからの初回アクセス」とみなし、ログイン画面へリダイレクトします。
 * </p>
 * <ul>
 *   <li>ブックマーク／ポータルなどからの入口として許容したパスにマッチしていること
 *   <li>Accept ヘッダが text/html を要求していること（AJAX ではない通常の HTML レスポンス）
 *   <li>HTTP メソッドが GET であること
 *   <li>セッション ID が送信されていない、または送信されたセッション ID が無効であること
 * </ul>
 * <p>
 * これにより、以下のようなケースを「ログイン画面へ誘導すべき初回アクセス」として扱います。
 * </p>
 * <ul>
 *   <li>ブラウザ起動直後に、ブックマークやポータルから入口ページ（初手アクセス想定 URL）へ直接アクセスした場合
 *   <li>一度ログイン後にタブを閉じ、セッションタイムアウトが発生したのち、
 *       再びブックマークやポータルから入口ページへアクセスした場合
 * </ul>
 * <p>
 * 上記条件に当てはまらないリクエスト（入口ページ以外の URL や、HTML 以外、AJAX、POST など）については、
 * 本クラスは 401 (Unauthorized) を返し、後段のエラー処理（ErrorController や ErrorViewResolver）に委ねます。
 * これにより、SPA／REST／AJAX では一貫して 401 を受け取り、クライアント側ロジックで
 * 「タイムアウト／未ログイン」の扱いを実装できるようにするとともに、
 * 動的 Web 画面では「タブ閉じ→ブックマーク再開」と「利用中タイムアウト」の UX を分けて制御できます。
 * </p>
 * <p>
 * 初手アクセスとして許容するパス（ブックマーク許容ページ群）はコンストラクタ引数
 * {@code bookmarkAwareEntryPointMatcher} で注入します。
 * これにより、アプリケーション側で「どの URL を利用開始地点と見なすか」を柔軟に定義できます。
 * </p>
 */
public class LoginOrTimeoutAuthenticationEntryPoint extends LoginUrlAuthenticationEntryPoint {

    /** {@link Logger}。 */
    private static final Logger logger = LoggerFactory.getLogger(LoginOrTimeoutAuthenticationEntryPoint.class);

    /** 初手アクセスとして想定したページへのリクエストであることを判定するMatcher。 */
    private final RequestMatcher browserInitialAccessMatcher;

    /**
     * コンストラクタ。
     * 
     * @param bookmarkAwareEntryPointMatcher ブックマークを許容する画面へのリクエストであることを判定する{@link RequestMatcher}
     * @param loginFormUrl                   ログイン画面のURL
     */
    public LoginOrTimeoutAuthenticationEntryPoint(
            RequestMatcher bookmarkAwareEntryPointMatcher,
            String loginFormUrl) {
        super(loginFormUrl);
        RequestMatcher browserGetRequestMatcher = browserGetRequestMatcher();
        this.browserInitialAccessMatcher = new AndRequestMatcher(bookmarkAwareEntryPointMatcher,
                browserGetRequestMatcher);
    }

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response,
            AuthenticationException authException) throws IOException, ServletException {
        // ブラウザからの初回アクセスと判断されれば、ログイン画面に誘導する
        if (browserInitialAccessMatcher.matches(request)) {
            if (request.getRequestedSessionId() == null) {
                // セッションIDを受信しなかった。
                // ブラウザ起動 → ブックマークから利用開始と判断
                super.commence(request, response, authException);
                return;
            } else if (!request.isRequestedSessionIdValid()) {
                // セッションIDを受信したがIDが無効だった。
                // タブ閉じ → （タイムアウト） → ブックマークから利用開始と判断
                super.commence(request, response, authException);
                return;
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
     * @return text/htmlに該当する AND AJAXではない AND GET要求
     */
    private RequestMatcher browserGetRequestMatcher() {
        MediaTypeRequestMatcher htmlMatcher = new MediaTypeRequestMatcher(MediaType.TEXT_HTML);
        htmlMatcher.setIgnoredMediaTypes(Collections.singleton(MediaType.ALL)); // */* は許容しない
        NegatedRequestMatcher notAjacMatcher = new NegatedRequestMatcher(
                new RequestHeaderRequestMatcher("X-Requested-With", "XMLHttpRequest"));
        RequestMatcher getMatcher = (HttpServletRequest request) -> request.getMethod().toUpperCase().equals("GET");
        return new AndRequestMatcher(htmlMatcher, notAjacMatcher, getMatcher);
    }

}
