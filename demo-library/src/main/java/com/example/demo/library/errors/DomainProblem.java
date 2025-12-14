package com.example.demo.library.errors;

import java.io.Serializable;

import org.jspecify.annotations.Nullable;
import org.springframework.context.MessageSourceResolvable;
import org.springframework.context.support.DefaultMessageSourceResolvable;

import com.fasterxml.jackson.annotation.JsonIgnore;

import io.swagger.v3.oas.annotations.media.Schema;

public class DomainProblem implements Serializable {

    @JsonIgnore
    private final MessageSourceResolvable resolvable;

    public DomainProblem(MessageSourceResolvable resolvable) {
        this.resolvable = resolvable;
    }

    public DomainProblem(String detail) {
        this.resolvable = new DefaultMessageSourceResolvable(null, detail);
    }

    public @Nullable String getType() {
        String[] codes = resolvable.getCodes();
        if (codes == null) {
            return null;
        } else if (codes.length == 1) {
            return codes[0];
        }
        return codes[codes.length - 1];
    }

    @Schema(type = "string")
    public MessageSourceResolvable getDetail() {
        return resolvable;
    }

    @Override
    public String toString() {
        return resolvable.toString();
    }
}
