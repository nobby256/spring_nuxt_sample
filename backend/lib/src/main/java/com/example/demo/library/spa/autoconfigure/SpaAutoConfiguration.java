package com.example.demo.library.spa.autoconfigure;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import org.springdoc.core.customizers.OpenApiCustomizer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.autoconfigure.web.WebProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.HttpMessageConverters;
import org.springframework.util.Assert;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.servlet.function.RouterFunction;
import org.springframework.web.servlet.function.RouterFunctions;
import org.springframework.web.servlet.function.ServerResponse;
import org.springframework.web.servlet.function.support.RouterFunctionMapping;

import com.example.demo.library.spa.AuthSessionRouterFunction;
import com.example.demo.library.spa.HistoryModeRouterFunction;
import com.example.demo.library.spa.IndexHtmlResourceFinder;
import com.example.demo.library.spa.SpaConfigurationProperties;

@AutoConfiguration
@ConditionalOnWebApplication
@EnableConfigurationProperties(SpaConfigurationProperties.class)
public class SpaAutoConfiguration {

	@Autowired
	private SpaConfigurationProperties spaProperties;

	// @Bean
	RouterFunction<ServerResponse> authSessionRouterFunction() {
		return AuthSessionRouterFunction.create(spaProperties);
	}

	// @Bean
	OpenApiCustomizer authSessionOpenApiCustomizer() {
		return AuthSessionRouterFunction.openApiCustomizer(spaProperties);
	}

	@Bean
	RouterFunctionMapping spaHistoryModeRouterFunctionMapping(IndexHtmlResourceFinder indexHtmlResourceFinder) {
		RouterFunctions.Builder builder = RouterFunctions.route();

		// AuthSessionRouterFunction
		// builder.add(AuthSessionRouterFunction.create(spaProperties));

		// HistoryModeRouterFunction
		String serverOrigin = spaProperties.getDevClient().getOrigin();
		Resource resource = indexHtmlResourceFinder.findResource();
		if (resource != null) {
			builder.add(HistoryModeRouterFunction.create(resource, serverOrigin));
		}

		HttpMessageConverters messageConverters = HttpMessageConverters.forServer().registerDefaults().build();
		List<HttpMessageConverter<?>> converters = StreamSupport.stream(messageConverters.spliterator(), false)
				.collect(Collectors.toList());
		RouterFunctionMapping mapping = new RouterFunctionMapping();
		mapping.setMessageConverters(converters);
		mapping.setRouterFunction(builder.build());
		// AdditionalHealthEndpointPathsWebMvcHandlerMapping:-100
		// WebMvcEndpointHandlerMapping:-100
		// ControllerEndpointHandlerMapping:-100
		// RouterFunctionMapping:-1
		// RequestMappingHandlerMapping:0
		// <ここに追加>:1
		// WelcomePageHandlerMapping:2
		// BeanNameUrlHandlerMapping:2
		// WelcomePageNotAcceptableHandlerMapping:LOWEST_PRECEDENCE - 10
		// SimpleUrlHandlerMapping(静的リソース用）:Ordered.LOWEST_PRECEDENCE - 1
		mapping.setOrder(1);

		return mapping;
	}

	@Bean
	IndexHtmlResourceFinder indexHtmlResourceFinder(
			ResourceLoader resourceLoader,
			WebProperties webProperties) {
		return new IndexHtmlResourceFinder(resourceLoader, webProperties.getResources());
	}

	@Configuration
	@ConditionalOnProperty(prefix = "spa.dev-client", name = "origin", matchIfMissing = false)
	class ClientConfiguration {

		private String getDevClientOrigin() {
			String origin = spaProperties.getDevClient().getOrigin();
			Assert.state(origin != null, "origin must not be null.");
			return origin;
		}

		/**
		 * クライアントのオリジンをCORSに登録する。
		 * <p>
		 * 戻り値の型（UrlBasedCorsConfigurationSource）と名前（corsConfigurationSource）が重要。<br/>
		 * この通りでないとSpringSecurityのCORS設定に自動登録されない。
		 * </p>
		 *
		 * @return {@link UrlBasedCorsConfigurationSource}
		 */
		@Bean("corsConfigurationSource")
		UrlBasedCorsConfigurationSource corsConfigurationSource() {
			String origin = getDevClientOrigin();

			CorsConfiguration corsConfiguration = new CorsConfiguration();
			corsConfiguration.addAllowedOrigin(origin);
			corsConfiguration.addAllowedMethod(CorsConfiguration.ALL);
			corsConfiguration.addAllowedHeader(CorsConfiguration.ALL);
			corsConfiguration.setAllowCredentials(true);

			UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
			source.registerCorsConfiguration("/**", corsConfiguration);
			return source;
		}
	}
}
