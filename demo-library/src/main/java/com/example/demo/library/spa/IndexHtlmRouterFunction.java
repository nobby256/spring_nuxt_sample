package com.example.demo.library.spa;

import static org.springframework.web.servlet.function.RequestPredicates.GET;
import static org.springframework.web.servlet.function.RequestPredicates.accept;

import java.util.Optional;

import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.web.servlet.function.RequestPredicate;
import org.springframework.web.servlet.function.RouterFunction;
import org.springframework.web.servlet.function.RouterFunctions;
import org.springframework.web.servlet.function.ServerResponse;

public class IndexHtlmRouterFunction {

    // 拡張子があるファイルを表す正規表現パターン
    private static final String PATTERN_WITH_EXT = ".*\\.[a-zA-Z0-9]+$";

    public static Optional<RouterFunction<ServerResponse>> create(IndexHtmlResourceFinder resourceFinder) {
        Resource resource = resourceFinder.findResource();
        if (resource == null) {
            return null;
        }
        RequestPredicate predicate = GET("/**").and(accept(MediaType.TEXT_HTML).and(match()));
        RouterFunction<ServerResponse> function = RouterFunctions.resource(predicate, resource);
        return Optional.ofNullable(function);
    }

    static RequestPredicate match() {
        return request -> {
            String path = request.path();
            if (!"/".equals(path)) {
                if (path.matches(PATTERN_WITH_EXT)) {
                    return false;
                }
            }
            return true;
        };
    }
}
