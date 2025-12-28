package com.example.demo.library.errors;

import java.net.URI;

import org.jspecify.annotations.Nullable;

public abstract class AbstractDomainProblem {

    private static URI TYPE = URI.create("/domain-error");

    private @Nullable String detail;

    public URI getType() {
        return TYPE;
    }

    public @Nullable String getDetail() {
        return detail;
    }

    public void setDetail(@Nullable String detail) {
        this.detail = detail;
    }

}
