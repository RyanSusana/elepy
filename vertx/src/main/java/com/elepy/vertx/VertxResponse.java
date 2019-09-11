package com.elepy.vertx;

import com.elepy.http.Response;
import io.vertx.core.http.HttpHeaders;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.impl.CookieImpl;


public class VertxResponse implements Response {

    static final String RESPONSE_KEY = "response";
    private final RoutingContext routingContext;
    private final HttpServerResponse response;

    public VertxResponse(RoutingContext routingContext) {
        this.routingContext = routingContext;
        this.response = routingContext.response();
        response.putHeader("Content-Type", "text/html");
    }

    @Override
    public void status(int statusCode) {
        response.setStatusCode(statusCode);
    }

    @Override
    public int status() {
        return response.getStatusCode();
    }

    @Override
    public void result(String body) {
        put(routingContext, RESPONSE_KEY, body);
    }

    @Override
    public void result(byte[] bytes) {
        put(routingContext, RESPONSE_KEY, bytes);
    }

    @Override
    public String result() {
        return routingContext.get(RESPONSE_KEY);
    }

    @Override
    public void type(String type) {
        response.putHeader("Content-Type", type);
    }

    @Override
    public String type() {
        return response.headers().get("Content-Type");
    }

    @Override
    public void removeCookie(String name) {
        routingContext.cookies().removeIf(cookie -> cookie.getName().equals(name));
    }

    @Override
    public void cookie(String name, String value) {
        routingContext.cookies().add(new CookieImpl(name, value));
    }

    @Override
    public void cookie(String name, String value, int maxAge) {
        final CookieImpl cookie = new CookieImpl(name, value);
        cookie.setMaxAge(maxAge);

        routingContext.cookies().add(cookie);

        routingContext.addCookie(cookie);

        routingContext.response().setChunked(true);
        routingContext.response().putHeader(HttpHeaders.ACCESS_CONTROL_ALLOW_ORIGIN, "*");
        routingContext.response().putHeader(HttpHeaders.ACCESS_CONTROL_ALLOW_METHODS, "*");

    }


    private void put(RoutingContext routingContext, String k, Object v) {
        routingContext.remove(k);
        routingContext.put(k, v);
    }

    @Override
    public void redirect(String location, int httpStatusCode) {
        header("Location", location);
        status(httpStatusCode);
    }

    @Override
    public void header(String s, String s1) {
        response.putHeader(s, s1);
    }

}
