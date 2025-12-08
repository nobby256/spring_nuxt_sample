package com.example.demo.config;

import org.springframework.boot.security.autoconfigure.actuate.web.servlet.EndpointRequest;
import org.springframework.boot.security.autoconfigure.web.servlet.PathRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.annotation.web.configurers.ExceptionHandlingConfigurer;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.logout.HttpStatusReturningLogoutSuccessHandler;

/**
 * SpringSecurityのConfigurationクラス。
 */
//@Configuration
public class SecurityConfiguration {

	/**
	 * {@link SecurityFilterChain}を作成する。
	 *
	 * @param http {@link HttpSecurity}
	 * @return {@link SecurityFilterChain}
	 */
	@Bean
	SecurityFilterChain securityFilterChain(HttpSecurity http) {

		http.formLogin(customizer -> {
			customizer.defaultSuccessUrl("/");
		}).authorizeHttpRequests(customizer -> {
			customizer.requestMatchers("/error/**").permitAll();
			customizer.anyRequest().authenticated();
		}).csrf(customizer -> {
			customizer.spa();
			customizer.ignoringRequestMatchers("/error/**");
		}).exceptionHandling(customizer -> {
			authenticationEntryPointFor(customizer);
		}).logout(customizer -> {
			customizer.logoutUrl("/api/logout").permitAll();
			customizer.logoutSuccessHandler(new HttpStatusReturningLogoutSuccessHandler(HttpStatus.NO_CONTENT));
		});

		return http.build();
	}

	/**
	 * 条件付き{@link AuthenticationEntryPoint}の登録。
	 * 
	 * @param customizer {@code ExceptionHandlingConfigurer<HttpSecurity>}
	 */
	void authenticationEntryPointFor(ExceptionHandlingConfigurer<HttpSecurity> customizer) {
		// 下記の条件に合致するリクエスト用のAuthenticationEntryPointを登録する。
		// ・GET以外
		// ・LoginConfigurerがdefaultAuthenticationEntryPointForで登録したmatcherに合致しない
		// (text/html かつ 非AJAX)
		customizer.defaultAuthenticationEntryPointFor((request, response, exception) -> {
			response.sendError(HttpStatus.UNAUTHORIZED.value());
		}, request -> !request.getMethod().toUpperCase().equals("GET"));
	}

	/**
	 * SpringSecurityの管理から除外するパスを設定する。
	 * 
	 * @return {@link WebSecurityCustomizer)
	 */
	@Bean
	WebSecurityCustomizer ignoringWebSecurityCustomizer() {
		return (web) -> {
			// 静的リソースを対象外(/css/**, /js/**, /images/**, /webjars/**, /favicon.*, /*/icon-*)
			web.ignoring().requestMatchers(PathRequest.toStaticResources().atCommonLocations());
			// Actuatorは対象外
			web.ignoring().requestMatchers(EndpointRequest.toAnyEndpoint());
		};
	}

}
