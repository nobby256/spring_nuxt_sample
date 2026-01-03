package com.example.demo.config;

import org.springdoc.core.customizers.OperationCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfiguration {

	@Bean
	OperationCustomizer operationIdCustomizer() {
		return (operation, handlerMethod) -> {
			String className = handlerMethod.getBeanType().getSimpleName();
			String methodName = handlerMethod.getMethod().getName();
			// 例: UserController_getUsers, UserController_createUser
			operation.setOperationId(className + "_" + methodName);
			return operation;
		};
	}

}
