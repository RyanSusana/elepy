package com.elepy.sparkjava;

import com.elepy.http.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import spark.RouteImpl;
import spark.Service;
import spark.route.HttpMethod;

import java.util.Map;
import java.util.Objects;
import java.util.TreeMap;

public class SparkService implements HttpService {

    private static final Logger logger = LoggerFactory.getLogger(SparkService.class);
    private final Service http;
    private final Map<RouteKey, Route> routes;
    private int counter;
    private boolean ignitedOnce = false;


    public SparkService() {
        this.http = Service.ignite();
        this.routes = new TreeMap<>();
    }

    @Override
    public void port(int port) {
        http.port(port);
    }

    @Override
    public int port() {
        return http.port();
    }

    public void notFound(spark.Route route) {
        http.notFound(route);
    }


    public void internalServerError(spark.Route route) {
        http.internalServerError(route);
    }

    public void stop() {
        http.stop();
        http.awaitStop();
    }

    @Override
    public void staticFiles(String path, StaticFileLocation location) {
        if (location.equals(StaticFileLocation.EXTERNAL)) {
            http.staticFiles.externalLocation(path);
        } else {
            http.staticFiles.location(path);
        }
    }

    public <T extends Exception> void exception(Class<T> exceptionClass, ExceptionHandler<? super T> handler) {
        http.exception(exceptionClass, ((e, req, res) -> handler.handleException(e, new SparkContext(req, res))));
    }

    @Override
    public void addRoute(Route route) {
        routes.put(new RouteKey(counter++), route);

        //auto-ignite route if ignite() already has been called
        if (ignitedOnce) {
            ignite();
        }
    }

    @Override
    public void ignite() {
        routes.forEach(((key, route) -> {
            if (!key.ignited) {
                key.ignited = true;
                igniteRoute(route);
            }
        }));
        ignitedOnce = true;

        http.awaitInitialization();
    }

    @Override
    public void before(String path, HttpContextHandler contextHandler) {
        http.before(path, (request, response) -> contextHandler.handle(new SparkContext(request, response)));
    }

    @Override
    public void before(HttpContextHandler contextHandler) {
        http.before((request, response) -> contextHandler.handle(new SparkContext(request, response)));
    }

    @Override
    public void after(String path, HttpContextHandler contextHandler) {
        http.before(path, (request, response) -> contextHandler.handle(new SparkContext(request, response)));
    }

    @Override
    public void after(HttpContextHandler contextHandler) {
        http.before((request, response) -> contextHandler.handle(new SparkContext(request, response)));
    }

    public void afterAfter(spark.Filter filter) {
        http.afterAfter(filter);
    }

    private void igniteRoute(Route extraRoute) {
        logger.debug(String.format("Ignited Route: [%s] %s", extraRoute.getMethod().name(), extraRoute.getPath()));

        http.addRoute(HttpMethod.get(extraRoute.getMethod().name().toLowerCase()), RouteImpl.create(extraRoute.getPath(), extraRoute.getAcceptType(), (request, response) -> {

            SparkContext sparkContext = new SparkContext(request, response);
            if (!extraRoute.getPermissions().isEmpty()) {
                sparkContext.requirePermissions(extraRoute.getPermissions());
            }
            extraRoute.getHttpContextHandler().handle(sparkContext);

            return response.body();
        }));

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
