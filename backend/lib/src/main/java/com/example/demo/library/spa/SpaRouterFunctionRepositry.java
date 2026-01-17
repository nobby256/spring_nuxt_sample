package com.example.demo.library.spa;

import org.springframework.web.servlet.function.RouterFunction;
import org.springframework.web.servlet.function.ServerResponse;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

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
