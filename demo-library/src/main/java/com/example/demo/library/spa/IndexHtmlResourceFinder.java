package com.example.demo.library.spa;

import java.io.IOException;
import java.io.UncheckedIOException;

import org.jspecify.annotations.Nullable;
import org.springframework.boot.autoconfigure.web.WebProperties.Resources;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;

public class IndexHtmlResourceFinder {

    private final ResourceLoader resourceLoader;
    private final Resources resources;

    public IndexHtmlResourceFinder(ResourceLoader resourceLoader, Resources resources) {
        this.resourceLoader = resourceLoader;
        this.resources = resources;
    }

    @Nullable
    public Resource findResource() {
        for (String location : resources.getStaticLocations()) {
            Resource indexHtml = findIndexHtmlResource(resourceLoader.getResource(location));
            if (indexHtml != null) {
                return indexHtml;
            }
        }
        return null;
    }

    @Nullable
    Resource findIndexHtmlResource(Resource location) {
        try {
            Resource resource = location.createRelative("/index.html");
            if (resource.exists() && resource.getURL() != null) {
                return resource;
            }
        } catch (IOException ex) {
            throw new UncheckedIOException(ex);
        }
        return null;
    }
}
