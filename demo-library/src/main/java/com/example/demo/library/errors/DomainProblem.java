package com.example.demo.library.errors;

import java.io.Serializable;
import java.net.URI;
import java.util.List;

import org.jspecify.annotations.Nullable;

import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema.RequiredMode;

public interface DomainProblem<T> extends Serializable {

    @Schema(requiredMode = RequiredMode.REQUIRED)
    URI getType();

    @Nullable
    String getDetail();

    void setDetail(@Nullable String detail);

    List<T> getErrors();

}
