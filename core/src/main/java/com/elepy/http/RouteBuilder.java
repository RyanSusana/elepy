package com.elepy.http;

import com.elepy.models.AccessLevel;

public final class RouteBuilder {
    private HttpContextHandler beforeFilter;
    private AccessLevel accessLevel;
    private HttpContextHandler route;
    private HttpMethod method;
    private String path;
    private String acceptType = "*/*";

    private RouteBuilder() {
    }

    public static RouteBuilder anElepyRoute() {
        return new RouteBuilder();
    }

    public RouteBuilder beforeFilter(HttpContextHandler beforeFilter) {
        this.beforeFilter = beforeFilter;
        return this;
    }

    public RouteBuilder accessLevel(AccessLevel accessLevel) {
        this.accessLevel = accessLevel;
        return this;
    }

    public RouteBuilder route(HttpContextHandler route) {
        this.route = route;
        return this;
    }

    public RouteBuilder method(HttpMethod method) {
        this.method = method;
        return this;
    }

    public RouteBuilder path(String path) {
        this.path = path;
        return this;
    }

    public RouteBuilder acceptType(String acceptType) {
        this.acceptType = acceptType;
        return this;
    }

    public Route build() {
        return new Route(path, method, beforeFilter, accessLevel, route, acceptType);
    }
}
