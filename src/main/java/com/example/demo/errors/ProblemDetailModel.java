package com.example.demo.errors;

import java.io.Serializable;
import java.net.URI;

import org.jspecify.annotations.Nullable;

public class ProblemDetailModel implements Serializable {

    private static final long serialVersionUID = 3307761915842206538L;

    private @Nullable URI type;

    private @Nullable String detail;

    protected ProblemDetailModel() {
    }

    public void setType(@Nullable URI type) {
        this.type = type;
    }

    public @Nullable URI getType() {
        return this.type;
    }

    public void setDetail(@Nullable String detail) {
        this.detail = detail;
    }

    public @Nullable String getDetail() {
        return this.detail;
    }

}
