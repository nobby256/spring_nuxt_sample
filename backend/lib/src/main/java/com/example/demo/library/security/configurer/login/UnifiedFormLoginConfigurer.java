package com.example.demo.library.security.configurer.login;

import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.HttpSecurityBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.FormLoginConfigurer;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

public class UnifiedFormLoginConfigurer extends UnifiedLoginConfigurer {

	public UnifiedFormLoginConfigurer() {
	}

	@Override
	public void loginPage(String loginPage) {
		getConfigurer().loginPage(loginPage);
	}

	@Override
	public void defaultSuccessUrl(String defaultSuccessUrl) {
		getConfigurer().defaultSuccessUrl(defaultSuccessUrl);
	}

	@Override
	public void defaultSuccessUrl(String defaultSuccessUrl, boolean alwaysUse) {
		getConfigurer().defaultSuccessUrl(defaultSuccessUrl, alwaysUse);
	}

	@Override
	public void successHandler(AuthenticationSuccessHandler successHandler) {
		getConfigurer().successHandler(successHandler);
	}

	@Override
	public void failureHandler(AuthenticationFailureHandler failureHandler) {
		getConfigurer().failureHandler(failureHandler);
	}

	@Override
	public void setBuilder(HttpSecurity http) {
		super.setBuilder(http);
		http.formLogin(Customizer.withDefaults());
	}

	@SuppressWarnings("unchecked")
	private FormLoginConfigurer<? extends HttpSecurityBuilder<?>> getConfigurer() {
		return getBuilder().getConfigurer(FormLoginConfigurer.class);
	}
}
