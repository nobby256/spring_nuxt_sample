package com.example.demo.library.security;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.function.Supplier;

import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.boot.actuate.autoconfigure.security.servlet.EndpointRequest;
import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.boot.autoconfigure.web.ServerProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.GenericApplicationContext;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.FormLoginConfigurer;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.authentication.ui.DefaultLoginPageGeneratingFilter;
import org.springframework.security.web.authentication.ui.DefaultResourcesFilter;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.security.web.csrf.CsrfTokenRequestAttributeHandler;
import org.springframework.security.web.csrf.CsrfTokenRequestHandler;
import org.springframework.security.web.csrf.XorCsrfTokenRequestAttributeHandler;
import org.springframework.security.web.servlet.util.matcher.PathPatternRequestMatcher;
import org.springframework.security.web.util.matcher.OrRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class HttpSecurityCustomizer {

    public interface StandardSettingCustomizer {
        void apply(StandardSetting customizer);
    }

    public static class StandardSetting {

        RequestMatcher bookmarkAwareEntryPointMatcher;
        boolean useDefaultLoginPage = true;

        StandardSetting() {
        }

        public void bookmarkAwareEntryPointMatchers(String... patterns) {
            List<RequestMatcher> matchers = Arrays.asList(patterns).stream()
                    .map(pattern -> (RequestMatcher) PathPatternRequestMatcher.withDefaults().matcher(pattern))
                    .toList();
            this.bookmarkAwareEntryPointMatcher = new OrRequestMatcher(matchers);
        }

        public void bookmarkAwareEntryPointMatchers(RequestMatcher... matchers) {
            this.bookmarkAwareEntryPointMatcher = new OrRequestMatcher(matchers);
        }

        RequestMatcher getBookmarkAwareEntryPointMatcher() {
            if (bookmarkAwareEntryPointMatcher == null) {
                return PathPatternRequestMatcher.withDefaults().matcher("/");
            }
            return bookmarkAwareEntryPointMatcher;
        }
    }

    public static HttpSecurity withStandardSettings(
            HttpSecurity http,
            StandardSettingCustomizer standardSettingCustomizer)
            throws Exception {

        StandardSetting setting = new StandardSetting();
        standardSettingCustomizer.apply(setting);

        http.logout(customizer -> {
            // セッションクッキーの破棄
            customizer.deleteCookies(createDeleteCookies(http));
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
                AuthenticationEntryPoint entryPoint = new LoginOrTimeoutAuthenticationEntryPoint(
                        setting.initialAccessEntryPointMatcher(), loginUrl);
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
