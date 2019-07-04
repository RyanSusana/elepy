package com.elepy.vertx;

import com.elepy.http.Session;
import io.vertx.ext.web.RoutingContext;

import java.util.Set;

public class VertxSession implements Session {


    private final RoutingContext routingContext;
    private final io.vertx.ext.web.Session vertxSession;

    public VertxSession(RoutingContext routingContext) {
        this.routingContext = routingContext;
        this.vertxSession = routingContext.session();
    }

    @Override
    public <T> T attribute(String name) {
        return null;
    }

    @Override
    public void attribute(String name, Object value) {

    }

    @Override
    public Set<String> attributes() {
        return null;
    }

    @Override
    public long creationTime() {
        return 0;
    }

    @Override
    public String id() {
        return null;
    }

    @Override
    public long lastAccessedTime() {
        return 0;
    }

    @Override
    public int maxInactiveInterval() {
        return 0;
    }

    @Override
    public void maxInactiveInterval(int interval) {

    }

    @Override
    public void invalidate() {

    }

    @Override
    public boolean isNew() {
        return false;
    }

    @Override
    public void removeAttribute(String name) {

    }
}
