package com.example.demo.library.errors;

import java.io.Serializable;
import java.net.URI;

import org.jspecify.annotations.Nullable;

import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema.RequiredMode;

public interface DomainProblem extends Serializable {

    URI TYPE_BASE = URI.create("/domain-error");

    @Schema(requiredMode = RequiredMode.REQUIRED)
    URI getType();

    String getDetail();

    void setDetail(@Nullable String detail);

}
