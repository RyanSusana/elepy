package com.elepy.vertx;

import com.elepy.http.Session;
import io.vertx.ext.web.RoutingContext;

import java.util.Set;

public class VertxSession implements Session {

    private final io.vertx.ext.web.Session session;

    public VertxSession(RoutingContext routingContext) {
        this.session = routingContext.session();
    }

    @Override
    public <T> T attribute(String name) {
        return session.get(name);
    }

    @Override
    public void attribute(String name, Object value) {
        session.put(name, value);
    }

    @Override
    public Set<String> attributes() {
        return session.data().keySet();
    }

    @Override
    public String id() {
        return session.id();
    }

}
