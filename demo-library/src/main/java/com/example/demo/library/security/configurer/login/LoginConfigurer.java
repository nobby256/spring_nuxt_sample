package com.example.demo.library.security.configurer.login;

import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

// public abstract class LoginConfigurer<T extends AbstractHttpConfigurer<T, HttpSecurity>>
//         extends AbstractHttpConfigurer<T, HttpSecurity> {
public abstract class LoginConfigurer {

    public abstract void loginPage(String loginPage);

    public abstract void successHandler(AuthenticationSuccessHandler successHandler);

    public abstract void failureHandler(AuthenticationFailureHandler failureHandler);

    public abstract void defaultSuccessUrl(String defaultSuccessUrl);

    public abstract void defaultSuccessUrl(String defaultSuccessUrl, boolean alwaysUse);

}
