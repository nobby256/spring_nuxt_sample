package com.example.demo.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.logout.HttpStatusReturningLogoutSuccessHandler;

import com.example.demo.library.security.UnifiedWebSecurity;

/**
 * SpringSecurityのConfigurationクラス。
 */
@Configuration
public class SecurityConfiguration {

	/**
	 * {@link SecurityFilterChain}を作成する。
	 *
	 * @param http    {@link HttpSecurity}
	 * @param unified {@link UnifiedWebSecurity)
	 * @return {@link SecurityFilterChain}
	 */
	@Bean
	SecurityFilterChain securityFilterChain(HttpSecurity http, UnifiedWebSecurity unified) {

		unified.login(http, customizer -> {
			customizer.defaultSuccessUrl("/");
		});
		unified.applyDefaults(http);

		http.authorizeHttpRequests(customizer -> {
			customizer.requestMatchers(unified.publicEndpoints()).permitAll();
			customizer.anyRequest().authenticated();
		});
		http.logout(customizer -> {
			customizer.logoutUrl("/api/logout").permitAll();
			customizer.logoutSuccessHandler(new HttpStatusReturningLogoutSuccessHandler(HttpStatus.NO_CONTENT));
		});

		return http.build();
	}

}
