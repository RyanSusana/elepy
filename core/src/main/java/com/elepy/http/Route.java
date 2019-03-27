package com.elepy.http;

import com.elepy.exceptions.ElepyConfigException;

import java.util.Objects;

/**
 * A route that can be added to {@link com.elepy.Elepy}
 */
public class Route {
    private final HttpContextHandler beforeFilter;
    private final AccessLevel accessLevel;
    private final HttpContextHandler httpContextHandler;
    private final HttpMethod method;
    private final String path;
    private final String acceptType;

    /**
     * @param path               The URI path
     * @param method             The HTTP method
     * @param beforeFilter       What happens before a httpContextHandler is executed
     * @param accessLevel        Who is allowed to see the httpContextHandler
     * @param httpContextHandler The Spark httpContextHandler interface
     * @param acceptType         The accept type of the httpContextHandler
     */
    public Route(String path, HttpMethod method, HttpContextHandler beforeFilter, AccessLevel accessLevel, HttpContextHandler httpContextHandler, String acceptType) {
        this.beforeFilter = beforeFilter == null ? ctx -> {
        } : beforeFilter;
        this.accessLevel = accessLevel == null ? AccessLevel.PUBLIC : accessLevel;
        this.acceptType = acceptType == null ? "*/*" : acceptType;
        if (httpContextHandler == null || path == null || method == null) {
            throw new ElepyConfigException("An elepy httpContextHandler must have a path, method and httpContextHandler");
        }
        this.httpContextHandler = httpContextHandler;
        this.method = method;
        this.path = path;
    }


    public HttpContextHandler getBeforeFilter() {
        return beforeFilter;
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
