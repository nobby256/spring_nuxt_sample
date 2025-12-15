package com.example.demo.library.errors.autoconfigure;

import java.util.List;
import java.util.Map;

import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.JacksonJsonHttpMessageConverter;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import com.example.demo.library.errors.DomainProblem;
import com.example.demo.library.errors.MessageSourceResolvableSerializer;

import tools.jackson.databind.json.JsonMapper;
import tools.jackson.databind.module.SimpleModule;

@AutoConfiguration
public class ErrorsAutoConfiguration {

    @Bean
    SimpleModule detailSerializerModule(MessageSource messageSource) {
        SimpleModule module = new SimpleModule("errorsModule");
        module.addSerializer(new MessageSourceResolvableSerializer(messageSource));
        return module;
    }

    @Bean
    WebMvcConfigurer domainProblemWebMvcConfigurer() {
        return new WebMvcConfigurer() {

            @Override
            public void extendMessageConverters(List<HttpMessageConverter<?>> converters) {
                for (HttpMessageConverter<?> converter : converters) {
                    if (converter instanceof JacksonJsonHttpMessageConverter jacksonConverter) {
                        jacksonConverter.registerMappersForType(DomainProblem.class,
                                (Map<MediaType, JsonMapper> registrations) -> {
                                    JsonMapper mapper = jacksonConverter.getMapper();
                                    registrations.put(MediaType.APPLICATION_PROBLEM_JSON, mapper);
                                });
                    }
                }
            }

        };
    }
}
