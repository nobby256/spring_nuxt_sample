package com.example.demo.library.spa;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;

import org.jspecify.annotations.Nullable;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.util.Assert;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.servlet.function.RouterFunction;
import org.springframework.web.servlet.function.RouterFunctions;
import org.springframework.web.servlet.function.ServerRequest;
import org.springframework.web.servlet.function.ServerResponse;
import org.springframework.web.servlet.function.support.RouterFunctionMapping;

public class SpaIndexHtlmRouterFunctionMapping extends RouterFunctionMapping {

	private static final String PATTERN_WITH_EXT = ".*\\.[a-zA-Z0-9]+$";

	// AdditionalHealthEndpointPathsWebMvcHandlerMapping:-100
	// WebMvcEndpointHandlerMapping:-100
	// ControllerEndpointHandlerMapping:-100
	// RouterFunctionMapping:-1
	// RequestMappingHandlerMapping:0
	// WelcomePageHandlerMapping:2
	// BeanNameUrlHandlerMapping:2
	// WelcomePageNotAcceptableHandlerMapping:LOWEST_PRECEDENCE - 10
	// SimpleUrlHandlerMapping(静的リソース用）:Ordered.LOWEST_PRECEDENCE - 1
	private static final int ORDER = 1;

	@Nullable
	private final Resource indexResource;

	public SpaIndexHtlmRouterFunctionMapping(@Nullable Resource indexResource) {
		this.indexResource = indexResource;

		setRouterFunction(createRouterFunction());
		setOrder(ORDER);
	}

	RouterFunction<ServerResponse> createRouterFunction() {
		return RouterFunctions.route().GET("/**", this::match, this::handle).build();
	}

	boolean match(ServerRequest request) {
		String path = request.path();
		if (!"/".equals(path)) {
			if (path.matches(PATTERN_WITH_EXT)) {
				return false;
			}
		}
		if (indexResource == null) {
			return false;
		}
		return true;
	}

	ServerResponse handle(ServerRequest request) {
		Assert.state(indexResource != null, "indexResource must not be null.");
		String html;
		try (InputStream istream = indexResource.getInputStream()) {
			html = FileCopyUtils.copyToString(new InputStreamReader(istream, StandardCharsets.UTF_8));
		} catch (IOException ex) {
			throw new UncheckedIOException(ex);
		}
		return ServerResponse.ok().contentType(MediaType.TEXT_HTML).body(html);
	}
}
