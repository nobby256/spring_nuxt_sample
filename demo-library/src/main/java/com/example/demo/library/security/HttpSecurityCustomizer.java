package com.example.demo.library.security;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.Objects;
import java.util.function.Supplier;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.actuate.autoconfigure.security.servlet.EndpointRequest;
import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.boot.autoconfigure.web.ServerProperties;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.ExceptionHandlingConfigurer;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.WebAttributes;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.authentication.logout.HttpStatusReturningLogoutSuccessHandler;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.security.web.csrf.CsrfTokenRequestAttributeHandler;
import org.springframework.security.web.csrf.CsrfTokenRequestHandler;
import org.springframework.security.web.csrf.XorCsrfTokenRequestAttributeHandler;
import org.springframework.security.web.servlet.util.matcher.PathPatternRequestMatcher;
import org.springframework.security.web.util.matcher.MediaTypeRequestMatcher;
import org.springframework.security.web.util.matcher.NegatedRequestMatcher;
import org.springframework.security.web.util.matcher.OrRequestMatcher;
import org.springframework.security.web.util.matcher.RequestHeaderRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.util.StringUtils;
import org.springframework.web.accept.ContentNegotiationStrategy;
import org.springframework.web.accept.HeaderContentNegotiationStrategy;
import org.springframework.web.util.WebUtils;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class HttpSecurityCustomizer {

    public static HttpSecurity withDefault(HttpSecurity http) throws Exception {
        http.logout(customizer -> {
            // REST用のログアウトハンドラ
            // defaultLogoutSuccessHandlerForなのでmatcherに該当しなければlogoutSuccessUrlが使用される
            customizer.defaultLogoutSuccessHandlerFor(
                    new HttpStatusReturningLogoutSuccessHandler(HttpStatus.NO_CONTENT), getRestMatcher(http));
            // セッションクッキーの破棄
            customizer.deleteCookies(createDeleteCookies(http));
            // 必要に応じて有効化
            // customizer.addLogoutHandler(new HeaderWriterLogoutHandler(new
            // ClearSiteDataHeaderWriter(Directive.ALL)));
        }).authorizeHttpRequests(customizer -> {
            // 静的リソースを対象外(/css/**, /js/**, /images/**, /webjars/**, /favicon.*, /*/icon-*)
            customizer.requestMatchers(PathRequest.toStaticResources().atCommonLocations()).permitAll();
            // Actuatorは対象外
            customizer.requestMatchers(EndpointRequest.toAnyEndpoint()).permitAll();
            // エラーコントローラは対象外
            customizer.requestMatchers(PathPatternRequestMatcher.withDefaults().matcher("/error/**")).permitAll();
            customizer.anyRequest().authenticated();
        }).sessionManagement(customizer -> {
            // URLリライティングを有効にする
            customizer.enableSessionUrlRewriting(true);
        }).exceptionHandling(customizer -> {
            // 【認証済み】
            customizer.accessDeniedHandler(new ForbiddenAccessDeniedHandler());

            // 【未認証】
            // REST向けのAuthenticationEntryPointを追加。
            // ポイントはauthenticationEntryPoint()はデフォルトに任せるという点。
            // authenticationEntryPoint()を設定しないと、HTML向けのログイン画面にリダイレクトするAuthenticationEntryPointが設定される。
            // その上で、REST向けのAuthenticationEntryPointを登録する。
            // 利用するメソッドがdefaultAuthenticationEntryPoint**For**()である点に注意。
            customizer.defaultAuthenticationEntryPointFor(new AuthenticationEntryPointForRest(), getRestMatcher(http));
        }).csrf(customizer -> {
            // Actuatorは対象外
            customizer.ignoringRequestMatchers(EndpointRequest.toAnyEndpoint());
            // エラーコントローラは対象外
            customizer.ignoringRequestMatchers(PathPatternRequestMatcher.withDefaults().matcher("/error/**"));

            // クッキーを使用したCSRFリポジトリを使用する（httpOnly=false）
            CookieCsrfTokenRepository repository = CookieCsrfTokenRepository.withHttpOnlyFalse();
            repository.setCookieCustomizer(cookieCustomizer -> {
                // server.servlet.session.cookie.secureと設定を連動させる（https時にはsecure=trueを推奨）
                ServerProperties serverProperties = SecurityBeanUtil.getBeanOrNull(http, ServerProperties.class);
                Boolean secure = serverProperties.getServlet().getSession().getCookie().getSecure();
                cookieCustomizer.secure(secure == null ? false : secure);
            });
            customizer.csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse());

            // SPA/MPA共用のカスタムハンドラを登録する
            customizer.csrfTokenRequestHandler(new SpaCsrfTokenRequestHandler());
        });

        return http;
    }

    /**
     * REST呼び出しを判定する{@link RequestMatcher}。
     * 
     * @param http {@link HttpSecurity}
     * @return {@link RequestMatcher}
     */
    static RequestMatcher getRestMatcher(HttpSecurity http) {
        ContentNegotiationStrategy contentNegotiationStrategy = http.getSharedObject(ContentNegotiationStrategy.class);
        if (contentNegotiationStrategy == null) {
            contentNegotiationStrategy = new HeaderContentNegotiationStrategy();
        }
        MediaTypeRequestMatcher htmlMatcher = new MediaTypeRequestMatcher(MediaType.TEXT_HTML);
        htmlMatcher.setIgnoredMediaTypes(Collections.singleton(MediaType.ALL));
        NegatedRequestMatcher notHtmlMatcher = new NegatedRequestMatcher(htmlMatcher);
        RequestMatcher ajaxMatcher = new RequestHeaderRequestMatcher("X-Requested-With", "XMLHttpRequest");
        // text/htmlに該当しない(*/*は許容せず) OR AJAXである
        return new OrRequestMatcher(Arrays.asList(ajaxMatcher, notHtmlMatcher));
    }

    /**
     * ログアウト時に削除するクッキー名の配列にセッションIDのクッキー名を追加する。
     *
     * @param http    {@link HttpSecurity}
     * @param cookies クッキー名
     * @return クッキーの配列
     */
    static String[] createDeleteCookies(HttpSecurity http, String... cookies) {
        String sessionCookie = sessionCookieName(http);
        String[] combineCookies;
        if (cookies != null) {
            combineCookies = new String[cookies.length + 1];
            combineCookies[0] = sessionCookie;
            System.arraycopy(cookies, 0, combineCookies, 1, cookies.length);
        } else {
            combineCookies = new String[] { sessionCookie };
        }
        return combineCookies;
    }

    /**
     * セッションIDのクッキー名を取得する。
     *
     * @param http {@link HttpSecurity}
     * @return セッションIDのクッキー名
     */
    static String sessionCookieName(HttpSecurity http) {
        ServerProperties properties = SecurityBeanUtil.getBeanOrNull(http, ServerProperties.class);
        Objects.requireNonNull(properties);
        String cookieName = properties.getServlet().getSession().getCookie().getName();
        if (cookieName == null) {
            cookieName = "JSESSIONID";
        }
        return cookieName;
    }

    /**
     * FORBIDDENになる{@link AccessDeniedHandler}。
     * <p>
     * {@link CookieCsrfTokenRepository}との併用を想定。
     * </p>
     */
    public static class ForbiddenAccessDeniedHandler implements AccessDeniedHandler {
        /** {@link Logger}。 */
        protected static final Logger logger = LoggerFactory.getLogger(ForbiddenAccessDeniedHandler.class);

        @Override
        public void handle(HttpServletRequest request, HttpServletResponse response,
                AccessDeniedException accessDeniedException) throws IOException, ServletException {
            if (response.isCommitted()) {
                logger.trace("Did not write to response since already committed");
                return;
            }
            // CSRFトークンにCookieを使用する為、セッションタイムアウト検出にAccessDeniedHandlerを使用しない
            // よって、下記の例外はすべてFORBIDDENとする。
            // ・MissingCsrfTokenException
            // ・InvalidCsrfTokenException
            // ・その他AccessDeniedExceptionの派生型
            logger.debug("Responding with 403 status code");
            response.sendError(HttpStatus.FORBIDDEN.value());
            request.setAttribute(WebUtils.ERROR_EXCEPTION_ATTRIBUTE, accessDeniedException);
            request.setAttribute(WebAttributes.ACCESS_DENIED_403, accessDeniedException);
        }
    }

    /**
     * REST用の{@link AuthenticationEntryPoint}。
     * <p>
     * {@link getRestMatcher}とセットで
     * {@link ExceptionHandlingConfigurer#defaultAuthenticationEntryPointFor(AuthenticationEntryPoint, RequestMatcher)}
     * で使用することを想定。
     * </p>
     */
    public static class AuthenticationEntryPointForRest implements AuthenticationEntryPoint {
        /** {@link Logger}。 */
        protected static final Logger logger = LoggerFactory.getLogger(AuthenticationEntryPointForRest.class);

        @Override
        public void commence(HttpServletRequest request, HttpServletResponse response,
                AuthenticationException authException) throws IOException, ServletException {
            if (response.isCommitted()) {
                logger.trace("Did not write to response since already committed");
                return;
            }
            // このクラス（AuthenticationEntryPoint）が呼び出される時点で未認証（anonymous）である事は大前提。
            // 以下の判定はシチュエーションの分類にすぎず、振る舞いとしては全て401という判定となる。
            String requestedSessionId = request.getRequestedSessionId();
            if (requestedSessionId == null) {
                // セッションIDが送られてこなかった場合
                // 純粋に初めてのアクセス。HTMLではないのでリダイレクトはしない。
                logger.debug("Responding with 401 status code");
                response.sendError(HttpStatus.UNAUTHORIZED.value());
            } else {
                // セッションIDが送られてきた場合
                boolean isRequestedSessionIdValid = request.isRequestedSessionIdValid();
                if (!isRequestedSessionIdValid) {
                    // セッションIDが送られてきて、かつ、そのIDが無効だった場合は**セッションタイムアウト**と判定
                    logger.debug("Responding with 401 status code");
                    response.sendError(HttpStatus.UNAUTHORIZED.value());
                } else {
                    // セッションIDが送られてきて、かつ、そのIDが有効、その状態で`anonymous`と判定されている点が重要。
                    // 未認証でも許されるAPIを利用している場合はセッションが既に存在しているため、この状態になる。
                    // 初回アクセスでセッション無しだろうが、既にアクセスしていてセッションありだろうが、
                    // 未認証（anonymous）である事には変わりがないので、挙動は401。
                    // （認証済みであれば403になる）
                    logger.debug("Responding with 401 status code");
                    response.sendError(HttpStatus.UNAUTHORIZED.value());
                }
            }
            request.setAttribute(WebUtils.ERROR_EXCEPTION_ATTRIBUTE, authException);
        }
    }

    /**
     * MPA/SPA両対応の{@link CsrfTokenRequestHandler}。
     * <p>
     * SpringSecurityのドキュメントで提示されているコードと同じクラスです。<br/>
     * https://docs.spring.io/spring-security/reference/servlet/exploits/csrf.html#csrf-integration-javascript-spa
     * </p>
     */
    static class SpaCsrfTokenRequestHandler implements CsrfTokenRequestHandler {
        /** リクエストヘッダ用のハンドラ。 */
        private final CsrfTokenRequestHandler plain = new CsrfTokenRequestAttributeHandler();
        /** リクエストパラメータ用のハンドラ。 */
        private final CsrfTokenRequestHandler xor = new XorCsrfTokenRequestAttributeHandler();

        @Override
        public void handle(HttpServletRequest request, HttpServletResponse response,
                Supplier<CsrfToken> csrfToken) {
            // hiddenタグ、metaタグが使用するXOR化したトークン値（BREACH対策）をリクエスト毎に準備します。
            xor.handle(request, response, csrfToken);

            // CSRFトークンはcsrfToken.get()を呼び出したときに発行されますが（無かった時のみジェネレート）、
            // このメソッドは標準構成ではformタグ、metaタグのレンダリング時のみ呼び出されます。
            // つまりHTMLを動的にレンダリングする機会がないシステムはCSRFトークンを発行する機会が無いことになります。
            // この典型例がOAuth2,SAML2認証のSPAです（FORM認証はログイン画面でformタグをレンダリングします）。
            // この問題に対応する為に、このハンドラはリクエストの度にトークン発行（無かった時のみジェネレート）を行います。
            // また、引数のcsrfTokenがnullだった場合、xor.handle()ではトークン削除、csrfToken.get()ではトークン新発行が行われます。
            // この一連の流れによってJSESSIONID同様のログイン/ログアウト時の洗い替えも実現します。
            csrfToken.get();
        }

        @Override
        public String resolveCsrfTokenValue(HttpServletRequest request, CsrfToken csrfToken) {
            // 最初にリクエストヘッダーのトークン（素の値）を確認し、無ければパラメーターのトークン（XOR化された値）を確認します
            String headerValue = request.getHeader(csrfToken.getHeaderName());
            return (StringUtils.hasText(headerValue) ? this.plain : this.xor).resolveCsrfTokenValue(request,
                    csrfToken);
        }
    }
}
