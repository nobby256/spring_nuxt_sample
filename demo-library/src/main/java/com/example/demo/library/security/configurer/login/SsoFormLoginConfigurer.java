package com.example.demo.library.security.configurer.login;

import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.FormLoginConfigurer;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

public class SsoFormLoginConfigurer extends LoginConfigurer {

    FormLoginConfigurer<HttpSecurity> configurer;

    public SsoFormLoginConfigurer(HttpSecurity http) {
        http.formLogin(customizer -> {
            configurer = customizer;
        });
    }

    @Override
    public void loginPage(String loginPage) {
        configurer.loginPage(loginPage);
    }

    @Override
    public void successHandler(AuthenticationSuccessHandler successHandler) {
        configurer.successHandler(successHandler);
    }

    @Override
    public void defaultSuccessUrl(String defaultSuccessUrl) {
        configurer.defaultSuccessUrl(defaultSuccessUrl);
    }

    @Override
    public void defaultSuccessUrl(String defaultSuccessUrl, boolean alwaysUse) {
        configurer.defaultSuccessUrl(defaultSuccessUrl, alwaysUse);
    }

    @Override
    public void failureHandler(AuthenticationFailureHandler failureHandler) {
        configurer.failureHandler(failureHandler);
    }

}
