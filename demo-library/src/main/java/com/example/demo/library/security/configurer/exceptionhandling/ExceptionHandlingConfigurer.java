package com.example.demo.library.security.configurer.exceptionhandling;

import java.util.Arrays;
import java.util.List;

import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.FormLoginConfigurer;
import org.springframework.security.web.authentication.ui.DefaultLoginPageGeneratingFilter;
import org.springframework.security.web.authentication.ui.DefaultResourcesFilter;
import org.springframework.security.web.servlet.util.matcher.PathPatternRequestMatcher;
import org.springframework.security.web.util.matcher.OrRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.util.Assert;

public class ExceptionHandlingConfigurer extends AbstractHttpConfigurer<ExceptionHandlingConfigurer, HttpSecurity> {

    private RequestMatcher bookmarkAwareEntryPointMatcher;

    /**
     * タブ閉じ → セッションタイムアウト → 再アクセス を行ったとき、
     * セッションタイムアウトではなくログイン画面へのリダイレクトを実行するパスパターン。
     * 
     * ブラウザ起動 → 初回アクセスの場合は、この設定に関係なく全てログイン画面にリダイレクトします
     *
     * @param patterns システムの初回アクセスを想定するパスパターン
     */
    public void bookmarkAwareEntryPointMatchers(String... patterns) {
        List<RequestMatcher> matchers = Arrays.asList(patterns).stream()
                .map(pattern -> (RequestMatcher) PathPatternRequestMatcher.withDefaults().matcher(pattern))
                .toList();
        this.bookmarkAwareEntryPointMatcher = new OrRequestMatcher(matchers);
    }

    RequestMatcher getBookmarkAwareEntryPointMatcher() {
        if (bookmarkAwareEntryPointMatcher == null) {
            return PathPatternRequestMatcher.withDefaults().matcher("/**");
        }
        return bookmarkAwareEntryPointMatcher;
    }

    @Override
    public void init(HttpSecurity http) {
        http.csrf(customizer -> {
            customizer.spa();
            // SpringSecurity6
            // // クッキーを使用したCSRFリポジトリを使用する
            // CookieCsrfTokenRepository repository =
            // CookieCsrfTokenRepository.withHttpOnlyFalse();
            // // SPA/MPA共用のカスタムハンドラを登録する
            // customizer.csrfTokenRequestHandler(new
            // SpaCompatibleCsrfTokenRequestHandler());
        });
        http.exceptionHandling(customizer -> {
            DefaultLoginPageGeneratingFilter filter = http.getSharedObject(DefaultLoginPageGeneratingFilter.class);
            Assert.state(filter != null, "filter must not be null.");
            String loginUrl = filter.getLoginPageUrl();
            RequestMatcher matcher = getBookmarkAwareEntryPointMatcher();

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
