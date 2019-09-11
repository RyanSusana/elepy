package com.elepy.http;

import com.elepy.exceptions.ElepyConfigException;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class HttpServiceConfiguration implements HttpService {

    private HttpService implementation;


    private boolean started = false;


    private List<Consumer<HttpService>> actions = new ArrayList<>();


    public HttpServiceConfiguration(HttpService implementation) {
        this.implementation = implementation;
    }

    private void add(Consumer<HttpService> action) {
        if (started) {
            action.accept(implementation);
        } else {
            this.actions.add(action);
        }
    }

    public void setImplementation(HttpService implementation) {
        this.implementation = implementation;
    }

    @Override
    public int port() {
        if (!started) {
            throw new ElepyConfigException("Server not yet started");
        } else {
            return implementation.port();
        }
    }

    @Override
    public void ignite() {
        if (implementation == null) {
            throw new ElepyConfigException("Please provide an implementation of HttpService to Elepy");
        }

        started = true;

        actions.forEach(httpServiceConsumer -> httpServiceConsumer.accept(implementation));
        implementation.ignite();
    }

    @Override
    public void stop() {
        add(HttpService::stop);
    }

    @Override
    public void ipAddress(String ip) {
        add(http -> http.ipAddress(ip));
    }


    @Override
    public void port(int port) {
        add(http -> http.port(port));
    }


    @Override
    public void addRoute(Route route) {
        add(http -> http.addRoute(route));
    }


    @Override
    public void staticFiles(String path, StaticFileLocation location) {
        add(http -> http.staticFiles(path, location));
    }

    @Override
    public <T extends Exception> void exception(Class<T> exceptionClass, ExceptionHandler<? super T> handler) {
        add(http -> http.exception(exceptionClass, handler));
    }

    @Override
    public void before(HttpContextHandler contextHandler) {
        add(http -> http.before(contextHandler));
    }

    @Override
    public void before(String path, HttpContextHandler contextHandler) {
        add(http -> http.before(path, contextHandler));
    }

    @Override
    public void after(String path, HttpContextHandler contextHandler) {

        add(http -> http.after(path, contextHandler));
    }

    @Override
    public void after(HttpContextHandler contextHandler) {
        add(http -> http.after(contextHandler));
    }
}
