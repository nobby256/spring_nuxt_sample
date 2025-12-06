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

    /** システムの初手アクセスとして許可されたページへのリクエストであることを判定する{@link RequestMatcher}。 */
    private RequestMatcher initialAccessEntryPointMatcher = PathPatternRequestMatcher.withDefaults().matcher("/");

    /** HTML要求（AJAXを除く）か否かを判定するパターン。 */
    private RequestMatcher htmlRequestMatcher;

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
        this.htmlRequestMatcher = htmlRequestMatcher();
    }

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response,
            AuthenticationException authException) throws IOException, ServletException {
        // HTML要求（AJAXを除く）か？
        if (htmlRequestMatcher.matches(request)) {
            if (request.getRequestedSessionId() == null) {
                // セッションクッキーが送信されなければ初回アクセスと判断し、ログイン画面にリダイレクトする
                super.commence(request, response, authException);
                return;
            } else if (!request.isRequestedSessionIdValid()) {
                // セッションIDが送信されてきたが、IDが無効だった場合はURLのパターンによって判断する
                if (initialAccessEntryPointMatcher.matches(request)) {
                    // リダイレクトする
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

    private RequestMatcher htmlRequestMatcher() {
        MediaTypeRequestMatcher htmlMatcher = new MediaTypeRequestMatcher(MediaType.TEXT_HTML);
        htmlMatcher.setIgnoredMediaTypes(Collections.singleton(MediaType.ALL));
        NegatedRequestMatcher notAjacMatcher = new NegatedRequestMatcher(
                new RequestHeaderRequestMatcher("X-Requested-With", "XMLHttpRequest"));
        // text/htmlに該当する(*/*は許容せず) AND AJAXではない
        return new AndRequestMatcher(htmlMatcher, notAjacMatcher);
    }

}
