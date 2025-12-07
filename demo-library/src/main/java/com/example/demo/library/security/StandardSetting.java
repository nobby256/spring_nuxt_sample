package com.example.demo.library.security;

import java.util.Arrays;
import java.util.List;

import org.springframework.security.web.servlet.util.matcher.PathPatternRequestMatcher;
import org.springframework.security.web.util.matcher.OrRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;

public class StandardSetting {
    private RequestMatcher bookmarkAwareEntryPointMatcher;

    StandardSetting() {
    }

    public void bookmarkAwareEntryPointMatchers(String... patterns) {
        List<RequestMatcher> matchers = Arrays.asList(patterns).stream()
                .map(pattern -> (RequestMatcher) PathPatternRequestMatcher.withDefaults().matcher(pattern))
                .toList();
        this.bookmarkAwareEntryPointMatcher = new OrRequestMatcher(matchers);
    }

    public void bookmarkAwareEntryPointMatchers(RequestMatcher... matchers) {
        this.bookmarkAwareEntryPointMatcher = new OrRequestMatcher(matchers);
    }

    RequestMatcher getBookmarkAwareEntryPointMatcher() {
        if (bookmarkAwareEntryPointMatcher == null) {
            return PathPatternRequestMatcher.withDefaults().matcher("/");
        }
        return bookmarkAwareEntryPointMatcher;
    }
}
