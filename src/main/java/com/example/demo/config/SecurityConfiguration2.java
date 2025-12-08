package com.example.demo.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.logout.HttpStatusReturningLogoutSuccessHandler;

import com.example.demo.library.security.StandardSecurityConfigurer;

/**
 * アプリケーションの設定クラス。
 */
@Configuration
public class SecurityConfiguration2 {

	/**
	 * SpringSecurityを設定する。
	 *
	 * @param http {@link HttpSecurity}
	 * @return {@link SecurityFilterChain}
	 */
	@Bean
	SecurityFilterChain securityFilterChain(HttpSecurity http, StandardSecurityConfigurer configurer) {

		configurer.applyDefaults(http);

		http.formLogin(customizer -> {
			customizer.defaultSuccessUrl("/");
		}).authorizeHttpRequests(customizer -> {
			customizer.requestMatchers(configurer.defaultPublicEndpoints()).permitAll();
			customizer.anyRequest().authenticated();
		}).logout(customizer -> {
			customizer.logoutUrl("/api/logout").permitAll();
			customizer.logoutSuccessHandler(new HttpStatusReturningLogoutSuccessHandler(HttpStatus.NO_CONTENT));
		});

		return http.build();
	}

}
