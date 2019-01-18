package com.elepy.models;

import com.elepy.exceptions.ElepyConfigException;
import spark.Filter;
import spark.Route;
import spark.route.HttpMethod;

import java.util.Objects;

/**
 * A route that can be added to {@link com.elepy.Elepy}
 */
public class ElepyRoute {
    private final Filter beforeFilter;
    private final AccessLevel accessLevel;
    private final Route route;
    private final HttpMethod method;
    private final String path;

    /**
     * @param path         The URI path
     * @param method       The HTTP method
     * @param beforeFilter What happens before a route is executed
     * @param accessLevel  Who is allowed to see the route
     * @param route        The Spark route interface
     */
    public ElepyRoute(String path, HttpMethod method, Filter beforeFilter, AccessLevel accessLevel, Route route) {
        this.beforeFilter = beforeFilter == null ? (req, res) -> {
        } : beforeFilter;
        this.accessLevel = accessLevel == null ? AccessLevel.PUBLIC : accessLevel;
        if (route == null || path == null || method == null) {
            throw new ElepyConfigException("An elepy route must have a path, method and route");
        }
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
