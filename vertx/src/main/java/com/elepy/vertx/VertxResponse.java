package com.elepy.vertx;

import com.elepy.http.Response;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.impl.CookieImpl;

import javax.servlet.http.HttpServletResponse;


public class VertxResponse implements Response {

    public final static String RESPONSE_KEY = "response";
    private final RoutingContext routingContext;
    private final HttpServerResponse response;

    public VertxResponse(RoutingContext routingContext) {
        this.routingContext = routingContext;
        this.response = routingContext.response();
        routingContext.put(RESPONSE_KEY, "");
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
        routingContext.put(RESPONSE_KEY, body);
    }

    @Override
    public void result(byte[] bytes) {
        routingContext.put(RESPONSE_KEY, bytes);
    }

    @Override
    public String result() {
        return routingContext.get(RESPONSE_KEY);
    }

    @Override
    public HttpServletResponse servletResponse() {
        return null;
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
    }

    @Override
    public void redirect(String location) {
        redirect(location, 301);
    }

    @Override
    public void redirect(String location, int httpStatusCode) {
        header("Loacation", location);
        status(httpStatusCode);
    }

    @Override
    public void header(String s, String s1) {
        response.putHeader(s, s1);
    }

}
