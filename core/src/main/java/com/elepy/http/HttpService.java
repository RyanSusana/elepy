package com.elepy.http;

import com.elepy.models.AccessLevel;

public interface HttpService {

    void ipAddress(String ip);

    void port(int port);

    void addRoute(Route route);

    void ignite();

    void stop();

    default void addRoute(HttpMethod httpMethod, String path, AccessLevel accessLevel, HttpContextHandler contextHandler) {
        addRoute(RouteBuilder
                .anElepyRoute()
                .route(contextHandler)
                .method(httpMethod)
                .path(path)
                .accessLevel(accessLevel)
                .build());
    }

    default void addRoute(HttpMethod httpMethod, String path, AccessLevel accessLevel, RequestResponseHandler requestResponseHandler) {
        addRoute(RouteBuilder
                .anElepyRoute()
                .route(ctx -> requestResponseHandler.handle(ctx.request(), ctx.response()))
                .method(httpMethod)
                .path(path)
                .accessLevel(accessLevel)
                .build());
    }


    ////////// FILTERS

    void before(String path, RequestResponseHandler requestResponseHandler);

    void before(String path, HttpContextHandler requestResponseHandler);

    void before(RequestResponseHandler requestResponseHandler);

    void before(HttpContextHandler requestResponseHandler);


    void after(String path, RequestResponseHandler requestResponseHandler);

    void after(String path, HttpContextHandler requestResponseHandler);

    void after(RequestResponseHandler requestResponseHandler);

    void after(HttpContextHandler requestResponseHandler);

    /////////// GET

    default void get(String path, RequestResponseHandler requestResponseHandler, AccessLevel accessLevel) {
        addRoute(HttpMethod.GET, path, accessLevel, requestResponseHandler);
    }

    default void get(String path, RequestResponseHandler requestResponseHandler) {
        addRoute(HttpMethod.GET, path, AccessLevel.PUBLIC, requestResponseHandler);
    }

    default void get(String path, HttpContextHandler contextHandler, AccessLevel accessLevel) {
        addRoute(HttpMethod.GET, path, accessLevel, contextHandler);
    }

    default void get(String path, HttpContextHandler contextHandler) {
        addRoute(HttpMethod.GET, path, AccessLevel.PUBLIC, contextHandler);
    }

    ////////// POST

    default void post(String path, RequestResponseHandler requestResponseHandler, AccessLevel accessLevel) {
        addRoute(HttpMethod.POST, path, accessLevel, requestResponseHandler);
    }

    default void post(String path, RequestResponseHandler requestResponseHandler) {
        addRoute(HttpMethod.POST, path, AccessLevel.PUBLIC, requestResponseHandler);
    }

    default void post(String path, HttpContextHandler contextHandler, AccessLevel accessLevel) {
        addRoute(HttpMethod.POST, path, accessLevel, contextHandler);
    }

    default void post(String path, HttpContextHandler contextHandler) {
        addRoute(HttpMethod.POST, path, AccessLevel.PUBLIC, contextHandler);
    }

    ///////// DELETE

    default void delete(String path, RequestResponseHandler requestResponseHandler, AccessLevel accessLevel) {
        addRoute(HttpMethod.DELETE, path, accessLevel, requestResponseHandler);
    }

    default void delete(String path, RequestResponseHandler requestResponseHandler) {
        addRoute(HttpMethod.DELETE, path, AccessLevel.PUBLIC, requestResponseHandler);
    }

    default void delete(String path, HttpContextHandler contextHandler, AccessLevel accessLevel) {
        addRoute(HttpMethod.DELETE, path, accessLevel, contextHandler);
    }

    default void delete(String path, HttpContextHandler contextHandler) {
        addRoute(HttpMethod.DELETE, path, AccessLevel.PUBLIC, contextHandler);
    }


    //////// PUT

    default void put(String path, RequestResponseHandler requestResponseHandler, AccessLevel accessLevel) {
        addRoute(HttpMethod.PUT, path, accessLevel, requestResponseHandler);
    }

    default void put(String path, RequestResponseHandler requestResponseHandler) {
        addRoute(HttpMethod.PUT, path, AccessLevel.PUBLIC, requestResponseHandler);
    }

    default void put(String path, HttpContextHandler contextHandler, AccessLevel accessLevel) {
        addRoute(HttpMethod.PUT, path, accessLevel, contextHandler);
    }

    default void put(String path, HttpContextHandler contextHandler) {
        addRoute(HttpMethod.PUT, path, AccessLevel.PUBLIC, contextHandler);
    }

    //////// PATCH

    default void patch(String path, RequestResponseHandler requestResponseHandler, AccessLevel accessLevel) {
        addRoute(HttpMethod.PATCH, path, accessLevel, requestResponseHandler);
    }

    default void patch(String path, RequestResponseHandler requestResponseHandler) {
        addRoute(HttpMethod.PATCH, path, AccessLevel.PUBLIC, requestResponseHandler);
    }

    default void patch(String path, HttpContextHandler contextHandler, AccessLevel accessLevel) {
        addRoute(HttpMethod.PATCH, path, accessLevel, contextHandler);
    }

    default void patch(String path, HttpContextHandler contextHandler) {
        addRoute(HttpMethod.PATCH, path, AccessLevel.PUBLIC, contextHandler);
    }

    /////// OPTIONS

    default void options(String path, RequestResponseHandler requestResponseHandler, AccessLevel accessLevel) {
        addRoute(HttpMethod.OPTIONS, path, accessLevel, requestResponseHandler);
    }

    default void options(String path, RequestResponseHandler requestResponseHandler) {
        addRoute(HttpMethod.OPTIONS, path, AccessLevel.PUBLIC, requestResponseHandler);
    }

    default void options(String path, HttpContextHandler contextHandler, AccessLevel accessLevel) {
        addRoute(HttpMethod.OPTIONS, path, accessLevel, contextHandler);
    }

    default void options(String path, HttpContextHandler contextHandler) {
        addRoute(HttpMethod.OPTIONS, path, AccessLevel.PUBLIC, contextHandler);
    }

}
