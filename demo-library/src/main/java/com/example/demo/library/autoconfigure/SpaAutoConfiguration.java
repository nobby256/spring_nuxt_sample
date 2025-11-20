package com.example.demo.library.autoconfigure;

import java.io.IOException;
import java.io.UncheckedIOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.autoconfigure.web.WebProperties;
import org.springframework.boot.autoconfigure.web.WebProperties.Resources;
import org.springframework.context.annotation.Bean;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.lang.Nullable;
import org.springframework.web.servlet.function.support.RouterFunctionMapping;

import com.example.demo.library.spa.SpaIndexHtlmRouterFunctionMapping;

@AutoConfiguration
@ConditionalOnWebApplication
public class SpaAutoConfiguration {

    private static final String INDEX_HTML_PATH = "/index.html";

    @Autowired
    private ResourceLoader resourceLoader;

    @Autowired
    private WebProperties webProperties;

    @Bean
    RouterFunctionMapping spaIndexRouterFunctionMapping() {
        Resource indexResource = getIndexHtmlResource();
        return new SpaIndexHtlmRouterFunctionMapping(indexResource);
    }

    @Nullable
    private Resource getIndexHtmlResource() {
        Resources resourceProperties = webProperties.getResources();
        for (String location : resourceProperties.getStaticLocations()) {
            Resource indexHtml = getIndexHtmlResource(resourceLoader.getResource(location));
            if (indexHtml != null) {
                return indexHtml;
            }
        }
        return null;
    }

    @Nullable
    private Resource getIndexHtmlResource(Resource location) {
        try {
            Resource resource = location.createRelative(INDEX_HTML_PATH);
            if (resource.exists() && resource.getURL() != null) {
                return resource;
            }
        } catch (IOException ex) {
            throw new UncheckedIOException(ex);
        }
        return null;
    }
}
