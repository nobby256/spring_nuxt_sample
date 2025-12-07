package com.example.demo.library.security;

import java.io.IOException;
import java.util.Collections;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.LoginUrlAuthenticationEntryPoint;
import org.springframework.security.web.servlet.util.matcher.PathPatternRequestMatcher;
import org.springframework.security.web.util.matcher.AndRequestMatcher;
import org.springframework.security.web.util.matcher.MediaTypeRequestMatcher;
import org.springframework.security.web.util.matcher.NegatedRequestMatcher;
import org.springframework.security.web.util.matcher.RequestHeaderRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.web.util.WebUtils;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class LoginOrTimeoutAuthenticationEntryPoint extends LoginUrlAuthenticationEntryPoint {

    /** {@link Logger}。 */
    private static final Logger logger = LoggerFactory.getLogger(LoginOrTimeoutAuthenticationEntryPoint.class);

    /** 初手アクセスとして想定したページへのリクエストであることを判定するMatcher。 */
    private final RequestMatcher initialAccessEntryPointMatcher;

    /** HTML要求（AJAXを除く）か否かを判定するMatcher。 */
    private final RequestMatcher htmlRequestMatcher;

    /**
     * コンストラクタ。
     * 
     * @param initialAccessEntryPointMatcher システムの初手アクセスとして許可されたページへのリクエストであることを判定する{@link RequestMatcher}
     * @param loginFormUrl                   ログイン画面のURL
     */
    public LoginOrTimeoutAuthenticationEntryPoint(
            RequestMatcher initialAccessEntryPointMatcher,
            String loginFormUrl) {
        super(loginFormUrl);
        this.initialAccessEntryPointMatcher = initialAccessEntryPointMatcher;
        this.htmlRequestMatcher = browserGetRequestMatcher();
    }

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response,
            AuthenticationException authException) throws IOException, ServletException {
        // HTML要求（AJAXを除く）か？
        if (htmlRequestMatcher.matches(request)) {
            if (request.getRequestedSessionId() == null) {
                // セッションIDを受信しなければ初回アクセスと判断し、ログイン画面にリダイレクトする
                super.commence(request, response, authException);
                return;
            } else if (!request.isRequestedSessionIdValid()) {
                // セッションIDを受信したが、IDが無効だった場合はURLのパターンによって判断する
                if (initialAccessEntryPointMatcher.matches(request)) {
                    // 初手想定URLの場合は利用開始と判断し、ログイン画面にリダイレクトする
                    // タブ閉じ→（タイムアウト）→再アクセスの対策
                    // 初手想定のURLに限り、タイムアウトではなくログイン誘導を優先する
                    // トレードオフとして利用中のタイムアウトは検出できなくなる
                    super.commence(request, response, authException);
                    return;
                }
            }
        }
        // HTML系の要求とREST系のレスポンスの違いはErrorControllerに任せる。
        // HTMLフラグメントを返すAJAXのレスポンスはカスタムのErrorViewResolverを登録してもらう事を想定。
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
