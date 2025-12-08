package com.example.demo.config;

import org.springframework.boot.security.autoconfigure.actuate.web.servlet.EndpointRequest;
import org.springframework.boot.security.autoconfigure.web.servlet.PathRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.logout.HttpStatusReturningLogoutSuccessHandler;

import com.example.demo.library.security.HttpSecuritySettings;

/**
 * アプリケーションの設定クラス。
 */
//@Configuration
public class SecurityConfiguration2 {

	/**
	 * SpringSecurityを設定する。
	 *
	 * @param http {@link HttpSecurity}
	 * @return {@link SecurityFilterChain}
	 */
	@Bean
	SecurityFilterChain securityFilterChain(HttpSecurity http) {

		HttpSecuritySettings.applyStandard(http, Customizer.withDefaults());

		http.formLogin(customizer -> {
			customizer.defaultSuccessUrl("/");
		}).authorizeHttpRequests(customizer -> {
			customizer.requestMatchers("/error/**").permitAll();
			customizer.anyRequest().authenticated();
		}).csrf(customizer -> {
			customizer.ignoringRequestMatchers("/error/**");
		}).logout(customizer -> {
			customizer.logoutUrl("/api/logout").permitAll();
			customizer.logoutSuccessHandler(new HttpStatusReturningLogoutSuccessHandler(HttpStatus.NO_CONTENT));
		});

		return http.build();
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
