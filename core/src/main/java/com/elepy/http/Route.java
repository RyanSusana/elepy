package com.elepy.http;

import com.elepy.exceptions.ElepyConfigException;
import com.elepy.models.AccessLevel;

import java.util.Objects;

/**
 * A route that can be added to {@link com.elepy.Elepy}
 */
public class Route {
    private final HttpContextHandler beforeFilter;
    private final AccessLevel accessLevel;
    private final HttpContextHandler route;
    private final HttpMethod method;
    private final String path;
    private final String acceptType;

    /**
     * @param path         The URI path
     * @param method       The HTTP method
     * @param beforeFilter What happens before a route is executed
     * @param accessLevel  Who is allowed to see the route
     * @param route        The Spark route interface
     * @param acceptType   The accept type of the route
     */
    public Route(String path, HttpMethod method, HttpContextHandler beforeFilter, AccessLevel accessLevel, HttpContextHandler route, String acceptType) {
        this.beforeFilter = beforeFilter == null ? ctx -> {
        } : beforeFilter;
        this.accessLevel = accessLevel == null ? AccessLevel.PUBLIC : accessLevel;
        this.acceptType = acceptType == null ? "*/*" : acceptType;
        if (route == null || path == null || method == null) {
            throw new ElepyConfigException("An elepy route must have a path, method and route");
        }
        this.route = route;
        this.method = method;
        this.path = path;
    }


    public HttpContextHandler getBeforeFilter() {
        return beforeFilter;
    }

    public HttpContextHandler getRoute() {
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
        Route that = (Route) o;
        return method == that.method &&
                Objects.equals(path, that.path);
    }

    @Override
    public int hashCode() {
        return Objects.hash(method, path);
    }

    public String getAcceptType() {
        return acceptType;
    }
}
