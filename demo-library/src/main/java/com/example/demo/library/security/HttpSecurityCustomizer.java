package com.example.demo.library.security;

import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.FormLoginConfigurer;
import org.springframework.security.web.authentication.ui.DefaultLoginPageGeneratingFilter;
import org.springframework.security.web.authentication.ui.DefaultResourcesFilter;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.util.Assert;

public class HttpSecurityCustomizer {

    public interface StandardSettingCustomizer {
        void apply(StandardSetting customizer);
    }

    public static HttpSecurity withStandardSettings(
            HttpSecurity http,
            StandardSettingCustomizer standardSettingCustomizer)
            throws Exception {

        StandardSetting setting = new StandardSetting();
        standardSettingCustomizer.apply(setting);

        http.logout(customizer -> {
            // セッションクッキーの破棄
            customizer.deleteCookies(HttpSecurityCustomizeUtil.createDeleteCookies(http));
        });
        http.csrf(customizer -> {
            customizer.spa();
            // SpringSecurity6
            // // クッキーを使用したCSRFリポジトリを使用する
            // CookieCsrfTokenRepository repository = CookieCsrfTokenRepository.withHttpOnlyFalse();
            // // SPA/MPA共用のカスタムハンドラを登録する
            // customizer.csrfTokenRequestHandler(new SpaCompatibleCsrfTokenRequestHandler());
        });

        // 静的リソースとアクチュエーターをSpringSecurityの対象外にする
        IgnoreStaticResourcesAndActuatorWebSecurityCustomizer.registerSingleton(http);

        http.with(new StandardSettingConfigurer(setting), Customizer.withDefaults());

        return http;
    }

    static class StandardSettingConfigurer extends AbstractHttpConfigurer<StandardSettingConfigurer, HttpSecurity> {

        private StandardSetting setting;

        public StandardSettingConfigurer(StandardSetting setting) {
            this.setting = setting;
        }

        @Override
        public void init(HttpSecurity http) {
            DefaultLoginPageGeneratingFilter filter = http.getSharedObject(DefaultLoginPageGeneratingFilter.class);
            Assert.state(filter != null, "filter must not be null.");
            String loginUrl = filter.getLoginPageUrl();
            RequestMatcher matcher = setting.getBookmarkAwareEntryPointMatcher();
            // ExceptionHandlingConfigurerはconfigureで設定を始めるのでinitなら間に合う
            http.exceptionHandling(customizer -> {
                customizer.authenticationEntryPoint(new LoginOrTimeoutAuthenticationEntryPoint(matcher, loginUrl));
                customizer.accessDeniedHandler(new CsrfAwareAccessDeniedHandler());
            });
        }

        @Override
        public void configure(HttpSecurity http) {
            // AuthenticationEntryPointを登録するので、デフォルトページが利用したくてもAbstractAuthenticationFilterConfigurer.configurerは
            // DefaultLoginPageGeneratingFilterを登録しない。
            // なので、LoginConfigurerを取得してカスタムページを準備済みか確認し、準備されていないようならばDefaultLoginPageGeneratingFilterを登録する。
            // なお、FORM認証だけを確認し、OAuth2/Saml2認証を確認していない理由は、
            // OAuth2/Saml2認証用のConfigurerは別JARなので、クラスパスに含まれているかどうかが不明である点と、
            // それらのIdPを利用する認証はIdP側でログイン画面を準備するのでデフォルトログイン画面は不要と判断。
            // なお、デフォルトログアウト画面は多分ニーズがないと判断し、登録しない。
            @SuppressWarnings({ "unchecked", "rawtypes" })
            FormLoginConfigurer formLoginConfigurer = http.getConfigurer(FormLoginConfigurer.class);
            if (formLoginConfigurer != null) {
                if (!formLoginConfigurer.isCustomLoginPage()) {
                    DefaultLoginPageGeneratingFilter filter = http
                            .getSharedObject(DefaultLoginPageGeneratingFilter.class);
                    Assert.state(filter != null, "filter must not be null.");
                    http.addFilter(filter);
                    http.addFilter(DefaultResourcesFilter.css());
                }
            }
        }

    }

}
