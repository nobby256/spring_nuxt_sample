package com.example.demo.library.security;

import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.util.matcher.RequestMatcher;

import com.example.demo.library.security.configurer.defaults.UnifiedDefaultsConfigurer;
import com.example.demo.library.security.configurer.endpoints.UnifiedPublicEndpointsProvider;
import com.example.demo.library.security.configurer.login.UnifiedLoginConfigurer;

public class UnifiedWebSecurity {

	private UnifiedPublicEndpointsProvider publicEndpointsProvider = new UnifiedPublicEndpointsProvider();
	private UnifiedDefaultsConfigurer defaultsConfigurer = new UnifiedDefaultsConfigurer();
	private UnifiedLoginConfigurer loginConfigurer;

	public UnifiedWebSecurity(UnifiedLoginConfigurer loginConfigurer) {
		this.loginConfigurer = loginConfigurer;
	}

	public UnifiedWebSecurity login(HttpSecurity http) {
		return login(http, Customizer.withDefaults());
	}

	public UnifiedWebSecurity login(HttpSecurity http, Customizer<UnifiedLoginConfigurer> customizer) {
		http.with(loginConfigurer, customizer);
		return this;
	}

	public UnifiedWebSecurity applyDefaults(HttpSecurity http) {
		return applyDefaults(http, Customizer.withDefaults());
	}

	public UnifiedWebSecurity applyDefaults(HttpSecurity http, Customizer<UnifiedDefaultsConfigurer> customizer) {
		http.with(defaultsConfigurer, customizer);
		return this;
	}

	public RequestMatcher publicEndpoints() {
		return publicEndpointsProvider.provide();
	}
}
