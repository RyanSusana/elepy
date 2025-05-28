package com.elepy;

import com.elepy.http.*;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class MockHttpService implements HttpService {
    @Override
    public void port(int port) {

    }

    @Override
    public int port() {
        return 0;
    }

    @Override
    public void ignite() {

    }

    @Override
    public void stop() {

    }

    @Override
    public void addRoute(Route route) {

    }

    @Override
    public void staticFiles(String path, StaticFileLocation location) {

    }

    @Override
    public <T extends Exception> void exception(Class<T> exceptionClass, ExceptionHandler<? super T> handler) {

    }

    @Override
    public void after(HttpContextHandler requestResponseHandler) {

    }

    @Override
    public void before(HttpContextHandler contextHandler) {

    }
}
