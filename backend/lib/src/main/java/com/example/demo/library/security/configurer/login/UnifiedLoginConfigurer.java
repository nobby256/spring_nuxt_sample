package com.example.demo.library.security.configurer.login;

import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

public abstract class UnifiedLoginConfigurer extends AbstractHttpConfigurer<UnifiedLoginConfigurer, HttpSecurity> {

    public abstract void loginPage(String loginPage);

    public abstract void defaultSuccessUrl(String defaultSuccessUrl);

    public abstract void defaultSuccessUrl(String defaultSuccessUrl, boolean alwaysUse);

    public abstract void successHandler(AuthenticationSuccessHandler successHandler);

    public abstract void failureHandler(AuthenticationFailureHandler failureHandler);

}
