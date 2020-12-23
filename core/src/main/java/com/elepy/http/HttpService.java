package com.elepy.http;


import com.elepy.exceptions.ElepyException;

public interface HttpService {

    void port(int port);

    int port();

    void addRoute(Route route);

    void ignite();

    void stop();

    default void staticFile(String path, String resourceLocation, String contentType, boolean gzip) {
        final var file = RouteBuilder.anElepyRoute().acceptType(contentType)
                .method(HttpMethod.GET)
                .path(path)
                .route(context -> {
                    context.type(contentType);
                    if (gzip)
                        context.response().header("Content-Encoding", "gzip");
                    final var resourceAsStream = getClass().getClassLoader().getResourceAsStream(resourceLocation);

                    if (resourceAsStream == null) {
                        throw new ElepyException(String.format("Resource '%s' not found", resourceLocation));
                    }
                    context.response().result(resourceAsStream);
                }).build();

        this.addRoute(file);
    }

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

    ////////// FILTERS


    void before(HttpContextHandler contextHandler);

    void before(String path, HttpContextHandler contextHandler);

    void after(String path, HttpContextHandler contextHandler);

    void after(HttpContextHandler contextHandler);


    /////////// GET

    default void get(String path, HttpContextHandler contextHandler) {
        addRoute(HttpMethod.GET, path, contextHandler);
    }

    ////////// POST

    default void post(String path, HttpContextHandler contextHandler) {
        addRoute(HttpMethod.POST, path, contextHandler);
    }

    ///////// DELETE

    default void delete(String path, HttpContextHandler contextHandler) {
        addRoute(HttpMethod.DELETE, path, contextHandler);
    }

    //////// PUT

    default void put(String path, HttpContextHandler contextHandler) {
        addRoute(HttpMethod.PUT, path, contextHandler);
    }

    //////// PATCH

    default void patch(String path, HttpContextHandler contextHandler) {
        addRoute(HttpMethod.PATCH, path, contextHandler);
    }

    /////// OPTIONS

    default void options(String path, HttpContextHandler contextHandler) {
        addRoute(HttpMethod.OPTIONS, path, contextHandler);
    }

}
