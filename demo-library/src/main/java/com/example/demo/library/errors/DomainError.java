package com.example.demo.library.errors;

import java.io.Serializable;

import org.jspecify.annotations.Nullable;
import org.springframework.context.MessageSourceResolvable;
import org.springframework.context.support.DefaultMessageSourceResolvable;

import com.fasterxml.jackson.annotation.JsonIgnore;

import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema.RequiredMode;

public class DomainError implements Serializable {

    @JsonIgnore
    protected final DefaultMessageSourceResolvable resolvable;

    public DomainError(DefaultMessageSourceResolvable resolvable) {
        this.resolvable = resolvable;
    }

    public @Nullable String getCode() {
        return resolvable.getCode();
    }

    @Schema(type = "string", requiredMode = RequiredMode.REQUIRED)
    public MessageSourceResolvable getMessage() {
        return resolvable;
    }

}
