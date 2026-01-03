package com.example.demo.library.spa;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.springframework.web.servlet.function.RouterFunction;
import org.springframework.web.servlet.function.ServerResponse;

public class SpaRouterFunctionRepositry {

	private List<RouterFunction<? extends ServerResponse>> functions = new ArrayList<>();

	public void addFunction(RouterFunction<? extends ServerResponse> function) {
		functions.add(function);
	}

	public List<RouterFunction<? extends ServerResponse>> getFunctions() {
		return Collections.unmodifiableList(functions);
	}

	public static interface SpaRouterFunctionRegister {
		void apply(SpaRouterFunctionRepositry repositry);
	}
}
