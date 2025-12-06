package com.example.demo.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

import com.example.demo.library.security.HttpSecurityCustomizer;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * アプリケーションの設定クラス。
 */
@Configuration
public class AppConfiguration {

	/**
	 * SpringSecurityを設定する。
	 *
	 * @param http {@link HttpSecurity}
	 * @return {@link SecurityFilterChain}
	 * @throws Exception 例外
	 */
	@Bean
	SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
		http.formLogin(customizer -> {
			customizer.defaultSuccessUrl("/");
		}).authorizeHttpRequests(customizer -> {
			customizer.requestMatchers("/error/**").permitAll();
			customizer.anyRequest().authenticated();
		}).csrf(customizer -> {
			customizer.ignoringRequestMatchers("/error/**");
		}).logout(customizer -> {
			customizer.logoutUrl("/api/logout").permitAll();
		});
		HttpSecurityCustomizer.withStandardSettings(http, customizer -> {
			customizer.authenticationEntryPointUrl("/login");
			customizer.initialAccessEntryPointPattern("/");
		});
		return http.build();
	}

	/**
	 * デフォルトの{@link ObjectMapper}を設定する。
	 * 
	 * @return {@link ObjectMapper}
	 */
	@Bean
	ObjectMapper objectMapper() {
		ObjectMapper objectMapper = new ObjectMapper();
		// NON_ABSENTの挙動は下記の通り
		// null、Optionalはシリアライズしない（undefined）。
		// ただし長さゼロの文字列、Emptyな配列はシリアライズする。
		objectMapper.setSerializationInclusion(JsonInclude.Include.NON_ABSENT);
		return objectMapper;
	}

}
