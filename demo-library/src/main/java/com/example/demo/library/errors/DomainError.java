package com.example.demo.library.errors;

import java.io.Serializable;

import org.springframework.context.MessageSourceResolvable;
import org.springframework.context.support.DefaultMessageSourceResolvable;

import io.swagger.v3.oas.annotations.media.Schema;

public class DomainError implements Serializable {

    protected final MessageSourceResolvable message;

    public DomainError(String message) {
        this(new DefaultMessageSourceResolvable(null, message));
    }

    public DomainError(MessageSourceResolvable message) {
        this.message = message;
    }

    @Schema(type = "string")
    public MessageSourceResolvable getMessage() {
        return message;
    }

}
