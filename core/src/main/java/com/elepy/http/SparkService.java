package com.elepy.http;

import com.elepy.Elepy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import spark.ExceptionHandler;
import spark.RouteImpl;
import spark.Service;
import spark.route.HttpMethod;

import java.util.Map;
import java.util.Objects;
import java.util.TreeMap;

public class SparkService implements HttpService {

    private final Service http;
    private final Elepy elepy;

    private Map<RouteKey, Route> routes;

    private int counter;

    private boolean ignitedOnce = false;


    private static final Logger logger = LoggerFactory.getLogger(SparkService.class);

    public SparkService(Service service, Elepy elepy) {
        this.http = service;
        this.elepy = elepy;
        this.routes = new TreeMap<>();
    }


    @Override
    public void ipAddress(String ip) {

        http.ipAddress(ip);
    }

    @Override
    public void port(int port) {
        http.port(port);
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

    public <T extends Exception> void exception(Class<T> exceptionClass, ExceptionHandler<? super T> handler) {
        http.exception(exceptionClass, handler);
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
    }

    @Override
    public void before(String path, RequestResponseHandler requestResponseHandler) {
        http.before(path, (request, response) -> requestResponseHandler.handle(new SparkRequest(request), new SparkResponse(response)));
    }

    @Override
    public void before(String path, HttpContextHandler requestResponseHandler) {
        http.before(path, (request, response) -> requestResponseHandler.handle(new SparkContext(request, response)));
    }

    @Override
    public void before(RequestResponseHandler requestResponseHandler) {
        http.before((request, response) -> requestResponseHandler.handle(new SparkRequest(request), new SparkResponse(response)));
    }

    @Override
    public void before(HttpContextHandler requestResponseHandler) {
        http.before((request, response) -> requestResponseHandler.handle(new SparkContext(request, response)));
    }


    @Override
    public void after(String path, RequestResponseHandler requestResponseHandler) {
        http.before(path, (request, response) -> requestResponseHandler.handle(new SparkRequest(request), new SparkResponse(response)));
    }

    @Override
    public void after(String path, HttpContextHandler requestResponseHandler) {
        http.before(path, (request, response) -> requestResponseHandler.handle(new SparkContext(request, response)));
    }

    @Override
    public void after(RequestResponseHandler requestResponseHandler) {
        http.before((request, response) -> requestResponseHandler.handle(new SparkRequest(request), new SparkResponse(response)));
    }

    @Override
    public void after(HttpContextHandler requestResponseHandler) {
        http.before((request, response) -> requestResponseHandler.handle(new SparkContext(request, response)));
    }

    public void afterAfter(spark.Filter filter) {
        http.afterAfter(filter);
    }

    private void igniteRoute(Route extraRoute) {
        logger.debug(String.format("Ignited Route: [%s] %s", extraRoute.getMethod().name(), extraRoute.getPath()));
        if (!extraRoute.getAccessLevel().equals(AccessLevel.DISABLED)) {
            http.addRoute(HttpMethod.get(extraRoute.getMethod().name().toLowerCase()), RouteImpl.create(extraRoute.getPath(), extraRoute.getAcceptType(), (request, response) -> {

                SparkContext sparkContext = new SparkContext(request, response);

                if (extraRoute.getAccessLevel().equals(AccessLevel.PROTECTED)) {
                    elepy.getAllAdminFilters().authenticate(sparkContext);
                }
                extraRoute.getBeforeFilter().handle(sparkContext);
                extraRoute.getHttpContextHandler().handle(sparkContext);

                return response.body();
            }));
        }
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
