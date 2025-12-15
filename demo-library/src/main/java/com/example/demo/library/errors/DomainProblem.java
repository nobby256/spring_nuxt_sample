package com.example.demo.library.errors;

import java.io.Serializable;
import java.util.List;

import org.jspecify.annotations.Nullable;

public interface DomainProblem<T> extends Serializable {

    default String getType() {
        return "/domain-problem";
    }

    @Nullable
    default String getDetail() {
        return "domain problem";
    }

    List<T> getErrors();

}
