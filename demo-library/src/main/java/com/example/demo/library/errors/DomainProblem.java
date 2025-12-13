package com.example.demo.library.errors;

import java.lang.reflect.UndeclaredThrowableException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Arrays;

import org.jspecify.annotations.Nullable;
import org.springframework.context.MessageSourceResolvable;

import com.fasterxml.jackson.annotation.JsonIgnore;

public class DomainProblem implements MessageSourceResolvable {

    @JsonIgnore
    private final @Nullable String code;

    @JsonIgnore
    private final Object @Nullable [] arguments;

    private final @Nullable URI type;

    private @Nullable String detail;

    public DomainProblem(@Nullable String code, Object @Nullable... arguments) {
        this.code = code;
        this.arguments = arguments;
        if (code != null) {
            try {
                this.type = new URI("/" + code.replace(".", "/"));
            } catch (URISyntaxException ex) {
                throw new UndeclaredThrowableException(ex);
            }
        } else {
            type = null;
        }
    }

    public DomainProblem() {
        this(null);
    }

    @Override
    public String[] getCodes() {
        return code != null ? new String[] { code } : new String[0];
    }

    @Override
    public Object @Nullable [] getArguments() {
        return arguments;
    }

    public @Nullable String getCode() {
        return code;
    }

    public @Nullable URI getType() {
        return type;
    }

    public String getDetail() {
        return detail;
    }

    public void setDetail(String detail) {
        this.detail = detail;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append(getClass().getName() + " [");
        builder.append(String.format("code=%s", code));
        if (arguments != null && arguments.length > 0) {
            builder.append(",");
            builder.append(String.format("arguments=%s", Arrays.toString(arguments)));
        }
        if (detail != null && detail.length() > 0) {
            builder.append(",");
            builder.append(String.format("detail=%s", detail));
        }
        builder.append("]");
        return builder.toString();
    }
}
