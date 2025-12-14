package com.example.demo.library.errors;

import java.io.Serializable;

import org.jspecify.annotations.Nullable;
import org.springframework.context.MessageSourceResolvable;
import org.springframework.context.support.DefaultMessageSourceResolvable;

import com.fasterxml.jackson.annotation.JsonIgnore;

import io.swagger.v3.oas.annotations.media.Schema;

public class ProblemMessage implements Serializable {

    @JsonIgnore
    private final DefaultMessageSourceResolvable resolvable;

    public ProblemMessage(String text) {
        this(new DefaultMessageSourceResolvable(null, text));
    }

    public ProblemMessage(DefaultMessageSourceResolvable resolvable) {
        this.resolvable = resolvable;
    }

    public @Nullable String getCode() {
        return resolvable.getCode();
    }

    @Schema(type = "string")
    public MessageSourceResolvable getText() {
        return resolvable;
    }

}
