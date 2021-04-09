package com.elepy.models.options;

import com.elepy.annotations.Dynamic;
import com.elepy.http.HttpMethod;

import java.lang.reflect.AnnotatedElement;

public class DynamicOptions implements Options {
    private final HttpMethod method;
    private final String path;
    private final boolean queryable;
    private final String mapper;

    public DynamicOptions(HttpMethod method, String path, boolean queryable, String mapper) {
        this.method = method;
        this.path = path;
        this.queryable = queryable;
        this.mapper = mapper;
    }

    public static Options of(AnnotatedElement field) {
        final var dynamic = field.getAnnotation(Dynamic.class);

        return new DynamicOptions(dynamic.method(), dynamic.path(), dynamic.queryable(), dynamic.mapper());
    }

    public HttpMethod getMethod() {
        return method;
    }

    public String getPath() {
        return path;
    }

    public boolean isQueryable() {
        return queryable;
    }

    public String getMapper() {
        return mapper;
    }
}
