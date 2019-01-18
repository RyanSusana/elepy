package com.elepy.models;

import spark.Filter;
import spark.Route;
import spark.route.HttpMethod;

public final class ElepyRouteBuilder {
    private Filter beforeFilter;
    private AccessLevel accessLevel;
    private Route route;
    private HttpMethod method;
    private String path;

    private ElepyRouteBuilder() {
    }

    public static ElepyRouteBuilder anElepyRoute() {
        return new ElepyRouteBuilder();
    }

    public ElepyRouteBuilder beforeFilter(Filter beforeFilter) {
        this.beforeFilter = beforeFilter;
        return this;
    }

    public ElepyRouteBuilder accessLevel(AccessLevel accessLevel) {
        this.accessLevel = accessLevel;
        return this;
    }

    public ElepyRouteBuilder route(Route route) {
        this.route = route;
        return this;
    }

    public ElepyRouteBuilder method(HttpMethod method) {
        this.method = method;
        return this;
    }

    public ElepyRouteBuilder path(String path) {
        this.path = path;
        return this;
    }

    public ElepyRoute build() {
        return new ElepyRoute(path, method, beforeFilter, accessLevel, route);
    }
}
