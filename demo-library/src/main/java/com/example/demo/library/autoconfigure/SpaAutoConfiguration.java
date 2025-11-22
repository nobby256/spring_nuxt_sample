package com.example.demo.library.autoconfigure;

import java.io.IOException;
import java.io.UncheckedIOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.autoconfigure.web.WebProperties;
import org.springframework.boot.autoconfigure.web.WebProperties.Resources;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.function.support.RouterFunctionMapping;

import com.example.demo.library.spa.SpaConfigurationProperties;
import com.example.demo.library.spa.SpaIndexHtlmRouterFunctionMapping;

@AutoConfiguration
@ConditionalOnWebApplication
@EnableConfigurationProperties(SpaConfigurationProperties.class)
public class SpaAutoConfiguration {

    @Autowired
    private ResourceLoader resourceLoader;

    @Autowired
    private WebProperties webProperties;

    @Autowired
    private SpaConfigurationProperties spaProperties;

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
            Resource resource = location.createRelative("/index.html");
            if (resource.exists() && resource.getURL() != null) {
                return resource;
            }
        } catch (IOException ex) {
            throw new UncheckedIOException(ex);
        }
        return null;
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

        /**
         * `/`へのアクセスをクライアントのサーバーにリダイレクトするViewControllerを登録する。
         * <p>
         * SpringBootは`/`要求を`forward:/index.html`に変換する機能をビルドインで提供します（{@code WelcomePageHandlerMapping}）。<br/>
         * 運用時はこの機能によって、`/resources/public/index.html`が`/`で公開されます。<br/>
         * しかし、クライアントのサーバーと連携するには`/`要求に対して`http://localhost:3000`へのリダイレクトを返す必要があります。<br/>
         * {@code WelcomePageHandlerMapping}よりも解決の優先度が高い{@code ViewController}を登録することで開発サーバとの連携を行います。
         * </p>
         * 
         * @return {@link WebMvcConfigurer}
         */
        @Bean
        WebMvcConfigurer redirectWebMvcConfigurer() {
            String origin = getDevClientOrigin();
            return new WebMvcConfigurer() {
                @Override
                public void addViewControllers(ViewControllerRegistry registry) {
                    registry.addRedirectViewController("/", origin + "/");
                }
            };
        }
    }
}
