package com.elepy.vertx;

import com.elepy.http.HttpContextHandler;
import com.elepy.http.HttpService;
import com.elepy.http.RequestResponseHandler;
import com.elepy.http.Route;

public class VertxService implements HttpService {
    @Override
    public void ipAddress(String ip) {

    }

    @Override
    public void port(int port) {

    }

    @Override
    public int port() {
        return 0;
    }

    @Override
    public void addRoute(Route route) {

    }

    @Override
    public void ignite() {

    }

    @Override
    public void stop() {

    }

    @Override
    public void before(String path, RequestResponseHandler requestResponseHandler) {

    }

    @Override
    public void before(String path, HttpContextHandler requestResponseHandler) {

    }

    @Override
    public void before(RequestResponseHandler requestResponseHandler) {

    }

    @Override
    public void before(HttpContextHandler requestResponseHandler) {

    }

    @Override
    public void after(String path, RequestResponseHandler requestResponseHandler) {

    }

    @Override
    public void after(String path, HttpContextHandler requestResponseHandler) {

    }

    @Override
    public void after(RequestResponseHandler requestResponseHandler) {

    }

    @Override
    public void after(HttpContextHandler requestResponseHandler) {

    }
}
