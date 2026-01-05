package com.example.demo.library.errors.autoconfigure;

import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;

import com.example.demo.library.errors.MessageSourceResolvableSerializer;

import tools.jackson.databind.module.SimpleModule;

@AutoConfiguration
public class ErrorsAutoConfiguration {

	@Bean
	SimpleModule detailSerializerModule(MessageSource messageSource) {
		SimpleModule module = new SimpleModule("errorsModule");
		module.addSerializer(new MessageSourceResolvableSerializer(messageSource));
		return module;
	}
}
