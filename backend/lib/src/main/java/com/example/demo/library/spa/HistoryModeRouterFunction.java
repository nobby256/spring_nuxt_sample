package com.example.demo.library.spa;

import static org.springframework.web.servlet.function.RequestPredicates.GET;
import static org.springframework.web.servlet.function.RequestPredicates.accept;

import java.net.URI;

import org.jspecify.annotations.Nullable;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.web.servlet.function.RequestPredicate;
import org.springframework.web.servlet.function.RouterFunction;
import org.springframework.web.servlet.function.RouterFunctions;
import org.springframework.web.servlet.function.ServerResponse;

public class HistoryModeRouterFunction {

	// 拡張子があるファイルを表す正規表現パターン
	private static final String PATTERN_WITH_EXT = ".*\\.[a-zA-Z0-9]+$";

	public static RouterFunction<ServerResponse> create(Resource resource, @Nullable String serverOrign) {

		RequestPredicate predicate = GET("/**")
				.and(accept(MediaType.TEXT_HTML).and(request -> match().test(request)));

		RouterFunctions.Builder builder = RouterFunctions.route();
		if (serverOrign == null) {
			builder.add(RouterFunctions.resource(predicate, resource));
		} else {
			builder.add(RouterFunctions.route(predicate,
					request -> ServerResponse.permanentRedirect(URI.create(serverOrign + request.path())).build()));
		}
		return builder.build();
	}

	static RequestPredicate match() {
		return request -> !request.path().matches(PATTERN_WITH_EXT);
	}
}
