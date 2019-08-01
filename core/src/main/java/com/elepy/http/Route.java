package com.elepy.http;

import com.elepy.exceptions.ElepyConfigException;

import java.util.Objects;
import java.util.Set;

/**
 * A route that can be added to {@link com.elepy.Elepy}
 */
public class Route {
    private final HttpContextHandler httpContextHandler;
    private final HttpMethod method;
    private final String path;
    private final String acceptType;
    private final Set<String> permissions;

    /**
     * @param path               The URI path
     * @param method             The HTTP method
     * @param httpContextHandler The Spark httpContextHandler interface
     * @param acceptType         The accept type of the httpContextHandler
     * @param permissions        The required permissions of the route.
     */
    public Route(String path, HttpMethod method, HttpContextHandler httpContextHandler, String acceptType, Set<String> permissions) {

        this.acceptType = acceptType == null ? "*/*" : acceptType;
        this.permissions = permissions;
        if (httpContextHandler == null || path == null || method == null) {
            throw new ElepyConfigException("An elepy httpContextHandler must have a path, method and httpContextHandler");
        }
        this.httpContextHandler = httpContextHandler;
        this.method = method;
        this.path = path;
    }


    public Set<String> getPermissions() {
        return permissions;
    }

    public HttpContextHandler getHttpContextHandler() {
        return httpContextHandler;
    }

    public HttpMethod getMethod() {
        return method;
    }

    public String getPath() {
        return path;
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
