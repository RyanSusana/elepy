package com.elepy.vertx;

import com.elepy.exceptions.ElepyConfigException;
import com.elepy.exceptions.ElepyException;
import com.elepy.http.*;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.http.HttpServer;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.handler.BodyHandler;
import io.vertx.ext.web.handler.CookieHandler;
import io.vertx.ext.web.handler.StaticHandler;
import org.eclipse.jetty.util.MultiMap;
import spark.HaltException;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.CountDownLatch;

public class VertxService implements HttpService {
    private final HttpServer server;
    private final Vertx vertx;
    private final Router router;
    private int port = 1337;
    private String ipAddress = "localhost";
    private Map<RouteKey, Route> routes;
    private int counter;
    private boolean ignitedOnce = false;

    private StaticHandler staticHandler = null;

    private Map<Class, ExceptionHandler> exceptionHandlers;
    private MultiMap<HttpContextHandler> before;
    private MultiMap<HttpContextHandler> after;

    public VertxService() {
        this.vertx = Vertx.vertx();

        this.server = vertx.createHttpServer();


        this.router = Router.router(vertx);

        this.exceptionHandlers = new HashMap<>();

        this.exceptionHandlers.put(Exception.class, (e, ctx) -> {
            e.printStackTrace();
        });

        this.before = new MultiMap<>();
        this.after = new MultiMap<>();
        this.routes = new HashMap<>();
    }

    @Override
    public void ipAddress(String ip) {
        this.ipAddress = ip;
    }

    @Override
    public void port(int port) {
        this.port = port;
    }

    @Override
    public int port() {
        return this.port;
    }

    @Override
    public void stop() {

        final CountDownLatch countDownLatch = new CountDownLatch(1);
        vertx.close(voidAsyncResult -> countDownLatch.countDown());

        try {
            countDownLatch.await();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    @Override
    public void staticFiles(String path, StaticFileLocation location) {
        staticHandler = StaticHandler.create().setWebRoot(path);
    }

    @Override
    public <T extends Exception> void exception(Class<T> exceptionClass, com.elepy.http.ExceptionHandler<? super T> handler) {
        exceptionHandlers.put(exceptionClass, handler);
    }


    @Override
    public void before(HttpContextHandler contextHandler) {
        this.before.put(null, contextHandler);
    }

    @Override
    public void before(String path, HttpContextHandler contextHandler) {
        this.before.put(path, contextHandler);
    }

    @Override
    public void after(String path, HttpContextHandler contextHandler) {
        this.after.put(path, contextHandler);
    }

    @Override
    public void after(HttpContextHandler contextHandler) {
        this.after.put(null, contextHandler);
    }

    @Override
    public void addRoute(Route route) {
        if (ignitedOnce) {
            throw new ElepyConfigException("Can't add routes after server has ignited");
        } else {
            this.routes.put(new RouteKey(counter++), route);
        }
    }

    @Override
    public void ignite() {

        router.route().handler(BodyHandler.create()
                .setMergeFormAttributes(true)
                .setUploadsDirectory(System.getProperty("java.io.tmpdir")));
        router.route().handler(CookieHandler.create());
        igniteBefore();
        igniteRoutes();
        igniteAfter();
        igniteStatic();
        igniteFinal();
        ignitedOnce = true;
        server.requestHandler(router);


        final CountDownLatch countDownLatch = new CountDownLatch(1);
        server.listen(port, httpServerAsyncResult -> countDownLatch.countDown());

        try {
            countDownLatch.await();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    private void igniteStatic() {
        if (staticHandler != null) {
            router.route().handler(staticHandler);
        }

    }

    private void igniteFinal() {
        router.route().handler(this::endRoute);

    }

    private void igniteAfter() {
        igniteMultiMapContexts(this.after);
    }

    private void igniteBefore() {
        igniteMultiMapContexts(this.before);
    }

    private Handler<RoutingContext> handleSafely(Handler<RoutingContext> h) {
        return routingContext -> {
            try {
                h.handle(routingContext);
            } catch (Exception e) {
                final ExceptionHandler exceptionHandler = getExceptionHandler(e.getClass());

                if (exceptionHandler != null)
                    exceptionHandler.handleException(e, new VertxContext(routingContext));

                endRoute(routingContext);
            }
        };
    }


    private void endRoute(RoutingContext routingContext) {
        final Object responseBody = routingContext.get(VertxResponse.RESPONSE_KEY);


        if (responseBody == null) {
            routingContext.response().end("<h2>404 Not Found</h2>");
        } else if (responseBody instanceof byte[]) {
            routingContext.response().end(Buffer.buffer((byte[]) responseBody));
        } else {
            routingContext.response().end(responseBody.toString());
        }
    }

    private void igniteMultiMapContexts(MultiMap<HttpContextHandler> contexts) {
        contexts.forEach((path, contextHandlers) ->
                //if path is null, use a route without a path
                Optional.ofNullable(path).map(router::route).orElse(router.route())
                        .handler(handleSafely(routingContext -> contextHandlers
                                .forEach(contextHandler -> {
                                    try {
                                        contextHandler.handle(new VertxContext(routingContext));
                                    } catch (HaltException e) {
                                        endRoute(routingContext);
                                    } catch (ElepyException e) {
                                        throw e;
                                    } catch (Exception e) {
                                        throw new ElepyException(e.getMessage(), 500, e);
                                    }
                                    routingContext.next();
                                }))));
    }


    private void igniteRoutes() {
        routes.forEach(((key, route) -> {
            if (!key.ignited) {
                key.ignited = true;
                igniteRoute(route);
            }
        }));
    }


    public ExceptionHandler getExceptionHandler(Class<? extends Exception> exceptionClass) {
        // If the exception map does not contain the provided exception class, it might
        // still be that a superclass of the exception class is.
        if (!this.exceptionHandlers.containsKey(exceptionClass)) {

            Class<?> superclass = exceptionClass.getSuperclass();
            do {
                // Is the superclass mapped?
                if (this.exceptionHandlers.containsKey(superclass)) {
                    // Use the handler for the mapped superclass, and cache handler
                    // for this exception class
                    ExceptionHandler handler = this.exceptionHandlers.get(superclass);
                    this.exceptionHandlers.put(exceptionClass, handler);
                    return handler;
                }

                // Iteratively walk through the exception class's superclasses
                superclass = superclass.getSuperclass();
            } while (superclass != null);

            // No handler found either for the superclasses of the exception class
            // We cache the null value to prevent future
            this.exceptionHandlers.put(exceptionClass, null);
            return null;
        }

        // Direct map
        return this.exceptionHandlers.get(exceptionClass);
    }

    private void igniteRoute(Route extraRoute) {
        router.route(transformToHttpMethod(extraRoute), extraRoute.getPath()).handler(
                handleSafely(routingContext -> {
                    final VertxContext vertxContext = new VertxContext(routingContext);
                    extraRoute.getHttpContextHandler().handleWithExceptions(vertxContext);

                    routingContext.next();
                }));
    }

    private io.vertx.core.http.HttpMethod transformToHttpMethod(Route route) {
        return io.vertx.core.http.HttpMethod.valueOf(route.getMethod().name());
    }

    private class RouteKey implements Comparable<RouteKey> {

        private final Integer id;

        private boolean ignited;


        private RouteKey(Integer id) {
            this.id = id;
            this.ignited = false;
        }


        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            RouteKey routeKey = (RouteKey) o;
            return Objects.equals(id, routeKey.id);
        }

        @Override
        public int hashCode() {
            return Objects.hash(id);
        }

        @Override
        public int compareTo(RouteKey o) {
            return id.compareTo(o.id);
        }
    }
}
