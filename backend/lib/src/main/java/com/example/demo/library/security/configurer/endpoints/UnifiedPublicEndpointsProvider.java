package com.example.demo.library.security.configurer.endpoints;

import org.springframework.boot.security.autoconfigure.actuate.web.servlet.EndpointRequest;
import org.springframework.boot.security.autoconfigure.web.servlet.PathRequest;
import org.springframework.security.web.servlet.util.matcher.PathPatternRequestMatcher;
import org.springframework.security.web.util.matcher.OrRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;

public class UnifiedPublicEndpointsProvider {

	public RequestMatcher provide() {
		return new OrRequestMatcher(
						PathPatternRequestMatcher.withDefaults().matcher("/**"),
						PathRequest.toStaticResources().atCommonLocations(),
						EndpointRequest.toAnyEndpoint());
	}

}
