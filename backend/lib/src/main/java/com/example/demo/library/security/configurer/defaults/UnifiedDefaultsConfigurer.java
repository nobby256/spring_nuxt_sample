package com.example.demo.library.security.configurer.defaults;

import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.util.matcher.RequestMatcher;

import com.example.demo.library.security.configurer.HttpSecurityCustomizeUtil;

public class UnifiedDefaultsConfigurer extends AbstractHttpConfigurer<UnifiedDefaultsConfigurer, HttpSecurity> {

	public UnifiedDefaultsConfigurer() {
	}

	@Override
	public void init(HttpSecurity http) {
		http.logout(customizer -> {
			customizer.deleteCookies(HttpSecurityCustomizeUtil.createDeleteCookies(http));
		});
		http.csrf(customizer -> {
			customizer.spa();
		});
		http.exceptionHandling(customizer -> {
			UnauthenticatedAuthenticationEntryPoint entryPoint = new UnauthenticatedAuthenticationEntryPoint();
			RequestMatcher matcher = new UnauthenticatedRequestMatcher(null);
			customizer.defaultAuthenticationEntryPointFor(entryPoint, matcher);
			customizer.accessDeniedHandler(new CsrfAwareAccessDeniedHandler());
		});
	}

	@Override
	public void configure(HttpSecurity http) {

	}
}
