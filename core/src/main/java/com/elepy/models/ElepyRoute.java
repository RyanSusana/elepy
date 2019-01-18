package com.elepy.models;

import spark.Filter;
import spark.Route;
import spark.route.HttpMethod;

import java.util.Objects;

public class ElepyRoute {
    private final Filter beforeFilter;
    private final AccessLevel accessLevel;
    private final Route route;
    private final HttpMethod method;
    private final String path;

    public ElepyRoute(String path, HttpMethod method, Filter beforeFilter, AccessLevel accessLevel, Route route) {
        this.beforeFilter = beforeFilter == null ? (req, res) -> {
        } : beforeFilter;
        this.accessLevel = accessLevel;
        this.route = route;
        this.method = method;
        this.path = path;
    }


    public Filter getBeforeFilter() {
        return beforeFilter;
    }

    public Route getRoute() {
        return route;
    }

    public HttpMethod getMethod() {
        return method;
    }

    public String getPath() {
        return path;
    }

    public AccessLevel getAccessLevel() {
        return accessLevel;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ElepyRoute that = (ElepyRoute) o;
        return method == that.method &&
                Objects.equals(path, that.path);
    }

    @Override
    public int hashCode() {
        return Objects.hash(method, path);
    }
}
