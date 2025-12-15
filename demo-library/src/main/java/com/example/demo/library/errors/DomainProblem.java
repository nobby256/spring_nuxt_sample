package com.example.demo.library.errors;

import java.io.Serializable;
import java.net.URI;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.commons.lang3.exception.UncheckedException;
import org.jspecify.annotations.Nullable;
import org.springframework.util.Assert;

public abstract class DomainProblem<T> implements Serializable {

    public static final String TYPE = "/domain-problem";
    protected static final String DETAIL = "domain problem";

    private final URI type;
    private final @Nullable String detail;
    private List<T> messages = new ArrayList<>();

    public DomainProblem() {
        this(TYPE);
    }

    public DomainProblem(String type) {
        this(TYPE, DETAIL);
    }

    public DomainProblem(String type, @Nullable String detail) {
        Assert.notNull(type, "type must not be null");
        try {
            this.type = new URI(type);
        } catch (Exception exception) {
            throw new UncheckedException(exception);
        }
        this.detail = detail;
    }

    public URI getType() {
        return type;
    }

    public @Nullable String getDetail() {
        return detail;
    }

    public void addMessage(T message) {
        messages.add(message);
    }

    public List<T> getMessages() {
        return Collections.unmodifiableList(messages);
    }

}
