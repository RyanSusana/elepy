package com.elepy.vertx;

import com.elepy.exceptions.ElepyConfigException;
import com.elepy.http.HttpContextHandler;
import com.elepy.http.HttpService;
import com.elepy.http.Route;
import io.vertx.core.Vertx;
import io.vertx.core.http.HttpServer;
import io.vertx.ext.web.Router;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class VertxService implements HttpService {
    private final HttpServer server;
    private final Vertx vertx;
    private final Router router;
    private int port = 1337;
    private String ipAddress = "localhost";
    private Map<RouteKey, Route> routes;
    private int counter;
    private boolean ignitedOnce = false;

    public VertxService() {
        this.vertx = Vertx.vertx();

        this.server = vertx.createHttpServer();
        this.router = Router.router(vertx);


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
        vertx.close();
    }

    @Override
    public void before(HttpContextHandler contextHandler) {

    }

    @Override
    public void before(String path, HttpContextHandler contextHandler) {

    }

    @Override
    public void after(String path, HttpContextHandler contextHandler) {

    }

    @Override
    public void after(HttpContextHandler contextHandler) {

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

        if (ignitedOnce) {
            throw new ElepyConfigException("Can't add routes after server has ignited");
        }
        routes.forEach(((key, route) -> {
            if (!key.ignited) {
                key.ignited = true;
                igniteRoute(route);
            }
        }));
        ignitedOnce = true;
        server.requestHandler(router);

        server.listen(port);
    }


    private void igniteRoute(Route extraRoute) {
        router.route(get(extraRoute), extraRoute.getPath()).handler(routingContext -> {

            final VertxContext vertxContext = new VertxContext(routingContext);

            try {
                extraRoute.getBeforeFilter().handle(vertxContext);
                extraRoute.getHttpContextHandler().handle(vertxContext);
            } catch (Exception e) {
                e.printStackTrace();
            }

            routingContext.response().end(routingContext.get(VertxResponse.RESPONSE_KEY).toString());


        });
    }

    private io.vertx.core.http.HttpMethod get(Route route) {
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
