package com.example.demo.library.security;

import java.util.Collections;
import java.util.Objects;
import java.util.function.Supplier;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.boot.actuate.autoconfigure.security.servlet.EndpointRequest;
import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.boot.autoconfigure.web.ServerProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.GenericApplicationContext;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.FormLoginConfigurer;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.DefaultRedirectStrategy;
import org.springframework.security.web.RedirectStrategy;
import org.springframework.security.web.authentication.logout.HttpStatusReturningLogoutSuccessHandler;
import org.springframework.security.web.authentication.ui.DefaultLoginPageGeneratingFilter;
import org.springframework.security.web.authentication.ui.DefaultResourcesFilter;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.security.web.csrf.CsrfTokenRequestAttributeHandler;
import org.springframework.security.web.csrf.CsrfTokenRequestHandler;
import org.springframework.security.web.csrf.XorCsrfTokenRequestAttributeHandler;
import org.springframework.security.web.servlet.util.matcher.PathPatternRequestMatcher;
import org.springframework.security.web.util.matcher.AndRequestMatcher;
import org.springframework.security.web.util.matcher.MediaTypeRequestMatcher;
import org.springframework.security.web.util.matcher.NegatedRequestMatcher;
import org.springframework.security.web.util.matcher.RequestHeaderRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;
import org.springframework.web.accept.ContentNegotiationStrategy;
import org.springframework.web.accept.HeaderContentNegotiationStrategy;
import org.springframework.web.util.WebUtils;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class HttpSecurityCustomizer {

    /** {@link Logger}。 */
    private static final Logger logger = LoggerFactory.getLogger(HttpSecurityCustomizer.class);

    public interface StandardSettingCustomizer {
        void apply(StandardSetting customizer);
    }

    public static class StandardSetting {

        String authenticationEntryPointUrl = "/login";
        RequestMatcher initialAccessEntryPointMatcher;
        boolean useDefaultLoginPage = true;

        StandardSetting() {
        }

        public void authenticationEntryPointUrl(String url) {
            this.authenticationEntryPointUrl = url;
        }

        public void initialAccessEntryPointPattern(String pattern) {
            this.initialAccessEntryPointMatcher = PathPatternRequestMatcher.withDefaults().matcher(pattern);
        }

        RequestMatcher initialAccessEntryPointMatcher() {
            if (initialAccessEntryPointMatcher == null) {
                return PathPatternRequestMatcher.withDefaults().matcher("/");
            }
            return initialAccessEntryPointMatcher;
        }
    }

    public static HttpSecurity withStandardSettings(
            HttpSecurity http,
            StandardSettingCustomizer standardSettingCustomizer)
            throws Exception {

        StandardSetting setting = new StandardSetting();
        standardSettingCustomizer.apply(setting);

        http.logout(customizer -> {
            // 非HTML要求用のログアウトハンドラ
            // defaultLogoutSuccessHandlerForなのでmatcherに該当しなければlogoutSuccessUrlが使用される
            customizer.defaultLogoutSuccessHandlerFor(
                    new HttpStatusReturningLogoutSuccessHandler(HttpStatus.NO_CONTENT),
                    new NegatedRequestMatcher(getWebAppMatcher(http)));
            // セッションクッキーの破棄
            customizer.deleteCookies(createDeleteCookies(http));
            // エラー画面から呼び出されることも考慮してログアウトは認証不要
            customizer.permitAll();
        });
        http.csrf(customizer -> {
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
            customizer.csrfTokenRequestHandler(new ExtendedCsrfTokenRequestHandler());
        });

        // 静的リソースをSpringSecurityの対象外にする
        registerWebSecurityCustomizer(http);

        http.with(new StandardSettingConfigurer(setting), Customizer.withDefaults());

        return http;
    }

    static class StandardSettingConfigurer extends AbstractHttpConfigurer<StandardSettingConfigurer, HttpSecurity> {

        private StandardSetting setting;

        public StandardSettingConfigurer(StandardSetting setting) {
            this.setting = setting;
        }

        @Override
        public void init(HttpSecurity http) throws Exception {
            DefaultLoginPageGeneratingFilter filter = http.getSharedObject(DefaultLoginPageGeneratingFilter.class);
            Assert.state(filter != null, "filter must not be null.");
            String loginUrl = filter.getLoginPageUrl();
            // ExceptionHandlingConfigurerはconfigureで設定を始めるのでinitなら間に合う
            http.exceptionHandling(customizer -> {
                AuthenticationEntryPoint entryPoint = authenticationEntryPoint(
                        http,
                        setting.initialAccessEntryPointMatcher(),
                        loginUrl);
                customizer.authenticationEntryPoint(entryPoint);
            });
        }

        @Override
        public void configure(HttpSecurity http) throws Exception {
            // AuthenticationEntryPointを登録するので、デフォルトページが利用したくてもAbstractAuthenticationFilterConfigurer.configurerは
            // DefaultLoginPageGeneratingFilterを登録しない。
            // なので、LoginConfigurerを取得してカスタムページを準備済みか確認し、準備されていないようならばDefaultLoginPageGeneratingFilterを登録する。
            // なお、FORM認証だけを確認し、OAuth2/Saml2認証を確認していない理由は、
            // OAuth2/Saml2認証用のConfigurerは別JARなので、クラスパスに含まれているかどうかが不明である点と、
            // それらのIdPを利用する認証はIdP側でログイン画面を準備するのでデフォルトログイン画面は不要と判断。
            @SuppressWarnings({ "unchecked", "rawtypes" })
            FormLoginConfigurer formLoginConfigurer = http.getConfigurer(FormLoginConfigurer.class);
            if (formLoginConfigurer != null) {
                // FORM認証で
                boolean useDefaultLoginPage = !formLoginConfigurer.isCustomLoginPage();
                if (useDefaultLoginPage) {
                    DefaultLoginPageGeneratingFilter filter = http
                            .getSharedObject(DefaultLoginPageGeneratingFilter.class);
                    Assert.state(filter != null, "filter must not be null.");
                    http.addFilter(filter);
                    http.addFilter(DefaultResourcesFilter.css());
                    // デフォルトログアウト画面は多分ニーズがないと判断し、登録しない。
                }
            }
        }

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

    static void registerWebSecurityCustomizer(HttpSecurity http) {
        ApplicationContext context = SecurityBeanUtil.getSharedOrBean(http, ApplicationContext.class);
        Assert.state(context != null, "context must not be null.");
        if (context instanceof GenericApplicationContext ctx) {
            ConfigurableListableBeanFactory beanFactory = ctx.getBeanFactory();
            beanFactory.registerSingleton("ignoreStaticResourcesAndActuatorWebSecurityCustomizer",
                    new WebSecurityCustomizer() {
                        @Override
                        public void customize(WebSecurity web) {
                            // 静的リソースを対象外(/css/**, /js/**, /images/**, /webjars/**, /favicon.*, /*/icon-*)
                            web.ignoring().requestMatchers(PathRequest.toStaticResources().atCommonLocations());
                            // Actuatorは対象外
                            web.ignoring().requestMatchers(EndpointRequest.toAnyEndpoint());
                        }
                    });
        }
    }

    static AuthenticationEntryPoint authenticationEntryPoint(
            HttpSecurity http,
            RequestMatcher initialAccessEntryMatcher,
            String entryPointUrl) {
        RequestMatcher webAppMatcher = getWebAppMatcher(http);
        RedirectStrategy redirectStrategy = new DefaultRedirectStrategy();
        return (HttpServletRequest request, HttpServletResponse response,
                AuthenticationException authException) -> {
            if (response.isCommitted()) {
                logger.trace("Did not write to response since already committed");
                return;
            }

            String url = request.getRequestURI();
            if (webAppMatcher.matches(request)) {
                if (initialAccessEntryMatcher.matches(request)) {
                    redirectStrategy.sendRedirect(request, response, entryPointUrl);
                    return;
                }
            }

            logger.debug("Responding with 401 status code");
            response.sendError(HttpStatus.UNAUTHORIZED.value());
            request.setAttribute(WebUtils.ERROR_EXCEPTION_ATTRIBUTE, authException);
        };
    }

    /**
     * WebApp（HTML系）呼び出しを判定する{@link RequestMatcher}を取得する。
     * 
     * @param http {@link HttpSecurity}
     * @return {@link RequestMatcher}
     */
    static RequestMatcher getWebAppMatcher(HttpSecurity http) {
        ContentNegotiationStrategy contentNegotiationStrategy = http.getSharedObject(ContentNegotiationStrategy.class);
        if (contentNegotiationStrategy == null) {
            contentNegotiationStrategy = new HeaderContentNegotiationStrategy();
        }
        MediaTypeRequestMatcher htmlMatcher = new MediaTypeRequestMatcher(MediaType.TEXT_HTML);
        htmlMatcher.setIgnoredMediaTypes(Collections.singleton(MediaType.ALL));
        NegatedRequestMatcher notAjacMatcher = new NegatedRequestMatcher(
                new RequestHeaderRequestMatcher("X-Requested-With", "XMLHttpRequest"));
        // text/htmlに該当しない(*/*は許容せず) OR AJAXである
        return new AndRequestMatcher(htmlMatcher, notAjacMatcher);
    }

    /**
     * MPA/SPA両対応の{@link CsrfTokenRequestHandler}。
     * <p>
     * SpringSecurityのドキュメントで提示されているコードと同じクラスです。<br/>
     * https://docs.spring.io/spring-security/reference/servlet/exploits/csrf.html#csrf-integration-javascript-spa
     * </p>
     */
    static class ExtendedCsrfTokenRequestHandler implements CsrfTokenRequestHandler {
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
