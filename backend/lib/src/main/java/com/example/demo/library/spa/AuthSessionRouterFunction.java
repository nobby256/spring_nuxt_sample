package com.example.demo.library.spa;

import java.util.List;

import org.springdoc.core.customizers.OpenApiCustomizer;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.web.servlet.function.HandlerFunction;
import org.springframework.web.servlet.function.RouterFunction;
import org.springframework.web.servlet.function.RouterFunctions;
import org.springframework.web.servlet.function.ServerRequest;
import org.springframework.web.servlet.function.ServerResponse;

import io.swagger.v3.core.converter.AnnotatedType;
import io.swagger.v3.core.converter.ModelConverters;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.Operation;
import io.swagger.v3.oas.models.PathItem;
import io.swagger.v3.oas.models.Paths;
import io.swagger.v3.oas.models.media.Content;
import io.swagger.v3.oas.models.media.MediaType;
import io.swagger.v3.oas.models.media.Schema;
import io.swagger.v3.oas.models.responses.ApiResponse;
import io.swagger.v3.oas.models.responses.ApiResponses;

public class AuthSessionRouterFunction {

	public static RouterFunction<ServerResponse> create(SpaConfigurationProperties spaProperties) {
		String path = spaProperties.getEndpoints().getAuthSessionPath();
		return RouterFunctions.route()
				.path(path, (RouterFunctions.Builder builder) -> {
					builder.GET(new GetHandler());
				})
				.build();
	}

	public static class GetHandler implements HandlerFunction<ServerResponse> {
		@Override
		public ServerResponse handle(ServerRequest request) {
			boolean isAuthenticated = false;
			String name = "anonymous";
			List<String> authorities = List.of();

			Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
			if (authentication != null) {
				isAuthenticated = authentication.isAuthenticated();
				if (authentication instanceof AnonymousAuthenticationToken) {
					name = authentication.getName();
				}
				authorities = authentication.getAuthorities().stream()
						.map(it -> it.getAuthority())
						.toList();
			}

			CsrfToken csrfToken =
					(CsrfToken) request.attribute(CsrfToken.class.getName()).orElseThrow();
			String token = csrfToken.getToken();

			AuthSessionResponse body =
					new AuthSessionResponse(name, authorities, isAuthenticated, token, csrfToken.getParameterName());

			return ServerResponse.ok()
					.contentType(org.springframework.http.MediaType.APPLICATION_JSON)
					.body(body);
		}
	}

	// public static class AuthSessionResponse {
	// private String user;
	// private List<String> authorities;
	// private boolean isAuthenticated;
	// private String csrfParameterToken;
	// private String csrfParameterName;

	// public AuthSessionResponse(
	// String user,
	// List<String> authorities,
	// boolean isAuthenticated,
	// String csrfParameterToken,
	// String csrfParameterName) {
	// this.user = user;
	// this.authorities = authorities;
	// this.isAuthenticated = isAuthenticated;
	// this.csrfParameterToken = csrfParameterToken;
	// this.csrfParameterName = csrfParameterName;
	// }

	// public String getUser() {
	// return user;
	// }

	// public List<String> getAuthorities() {
	// return authorities;
	// }

	// public boolean isAuthenticated() {
	// return isAuthenticated;
	// }

	// public String getCsrfParameterToken() {
	// return csrfParameterToken;
	// }

	// public String getCsrfParameterName() {
	// return csrfParameterName;
	// }

	// }

	public static record AuthSessionResponse(
			String user,
			List<String> authorities,
			boolean isAuthenticated,
			String csrfParameterToken,
			String csrfParameterName) {}

	public static OpenApiCustomizer openApiCustomizer(SpaConfigurationProperties spaProperties) {
		String path = spaProperties.getEndpoints().getAuthSessionPath();
		return (OpenAPI openApi) -> {
			// 1. AuthSessionResponse クラスから Schema を生成して components に登録
			var converters = ModelConverters.getInstance();
			var type = new AnnotatedType(AuthSessionResponse.class);

			// 生成された schema 群（1クラスでも Map で返ってくる）
			var schemas = converters.readAll(type);
			if (openApi.getComponents() == null) {
				openApi.setComponents(new io.swagger.v3.oas.models.Components());
			}
			openApi.getComponents().getSchemas().putAll(schemas);

			// 2. その schema への $ref を組み立てる
			String schemaName = schemas.keySet().iterator().next(); // たいてい "AuthSessionResponse"
			Schema<?> schemaRef = new Schema<>().$ref("#/components/schemas/" + schemaName);

			MediaType mediaType = new MediaType().schema(schemaRef);
			Content content = new Content().addMediaType("application/json", mediaType);
			ApiResponse ok = new ApiResponse().content(content);
			ApiResponses responses = new ApiResponses().addApiResponse("200", ok);

			// 3. Operation を作って path に登録
			if (openApi.getPaths() == null) {
				openApi.setPaths(new Paths());
			}
			PathItem pathItem = openApi.getPaths().computeIfAbsent(path, p -> new PathItem());

			Operation getOperation =
					new Operation().operationId("getAuthSession").responses(responses);

			pathItem.setGet(getOperation);
			openApi.getPaths().addPathItem(path, pathItem);
		};
	}
}
