package com.example.demo.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.logout.HttpStatusReturningLogoutSuccessHandler;

import com.example.demo.library.security.HttpSecurityDefaults;

/**
 * SpringSecurityのConfigurationクラス。
 */
@Configuration
public class SecurityConfiguration {

	/**
	 * {@link SecurityFilterChain}を作成する。
	 *
	 * @param http     {@link HttpSecurity}
	 * @param defaults {@link HttpSecurityDefaults)
	 * @return {@link SecurityFilterChain}
	 */
	@Bean
	SecurityFilterChain securityFilterChain(HttpSecurity http, HttpSecurityDefaults defaults) {

		defaults.login(http, customizer -> {
			customizer.defaultSuccessUrl("/");
		});
		defaults.apply(http);

		http.authorizeHttpRequests(customizer -> {
			customizer.requestMatchers(defaults.publicEndpoints()).permitAll();
			customizer.anyRequest().authenticated();
		});
		http.logout(customizer -> {
			customizer.logoutUrl("/api/logout").permitAll();
			customizer.logoutSuccessHandler(new HttpStatusReturningLogoutSuccessHandler(HttpStatus.NO_CONTENT));
		});

		return http.build();
	}

}
