package com.elepy.http;


public interface HttpService {

    void ipAddress(String ip);

    void port(int port);

    int port();

    void addRoute(Route route);

    void ignite();

    void stop();

    void staticFiles(String path, StaticFileLocation location);

    default void staticFiles(String path) {
        staticFiles(path, StaticFileLocation.CLASSPATH);
    }

    <T extends Exception> void exception(Class<T> exceptionClass, ExceptionHandler<? super T> handler);

    default void addRoute(HttpMethod method, String path, HttpContextHandler contextHandler) {
        addRoute(RouteBuilder
                .anElepyRoute()
                .route(contextHandler)
                .method(method)
                .path(path)
                .build());
    }

    default void addRoute(HttpMethod method, String path, RequestResponseHandler requestResponseHandler) {
        addRoute(RouteBuilder
                .anElepyRoute()
                .route(ctx -> requestResponseHandler.handle(ctx.request(), ctx.response()))
                .method(method)
                .path(path)
                .build());
    }


    ////////// FILTERS


    void before(HttpContextHandler contextHandler);

    void before(String path, HttpContextHandler contextHandler);

    void after(String path, HttpContextHandler contextHandler);

    void after(HttpContextHandler contextHandler);

    default void before(String path, RequestResponseHandler requestResponseHandler) {
        before(path, context -> requestResponseHandler.handle(context.request(), context.response()));
    }

    default void before(RequestResponseHandler requestResponseHandler) {
        before(context -> requestResponseHandler.handle(context.request(), context.response()));
    }

    default void after(String path, RequestResponseHandler requestResponseHandler) {
        after(path, context -> requestResponseHandler.handle(context.request(), context.response()));
    }

    default void after(RequestResponseHandler requestResponseHandler) {
        after(context -> requestResponseHandler.handle(context.request(), context.response()));
    }


    /////////// GET

    default void get(String path, RequestResponseHandler requestResponseHandler) {
        addRoute(HttpMethod.GET, path, requestResponseHandler);
    }

    default void get(String path, HttpContextHandler contextHandler) {
        addRoute(HttpMethod.GET, path, contextHandler);
    }

    ////////// POST

    default void post(String path, RequestResponseHandler requestResponseHandler) {
        addRoute(HttpMethod.POST, path, requestResponseHandler);
    }

    default void post(String path, HttpContextHandler contextHandler) {
        addRoute(HttpMethod.POST, path, contextHandler);
    }

    ///////// DELETE

    default void delete(String path, RequestResponseHandler requestResponseHandler) {
        addRoute(HttpMethod.DELETE, path, requestResponseHandler);
    }

    default void delete(String path, HttpContextHandler contextHandler) {
        addRoute(HttpMethod.DELETE, path, contextHandler);
    }


    //////// PUT

    default void put(String path, RequestResponseHandler requestResponseHandler) {
        addRoute(HttpMethod.PUT, path, requestResponseHandler);
    }

    default void put(String path, HttpContextHandler contextHandler) {
        addRoute(HttpMethod.PUT, path, contextHandler);
    }
    //////// PATCH

    default void patch(String path, RequestResponseHandler requestResponseHandler) {
        addRoute(HttpMethod.PATCH, path, requestResponseHandler);
    }

    default void patch(String path, HttpContextHandler contextHandler) {
        addRoute(HttpMethod.PATCH, path, contextHandler);
    }

    /////// OPTIONS

    default void options(String path, RequestResponseHandler requestResponseHandler) {
        addRoute(HttpMethod.OPTIONS, path, requestResponseHandler);
    }

    default void options(String path, HttpContextHandler contextHandler) {
        addRoute(HttpMethod.OPTIONS, path, contextHandler);
    }

}
