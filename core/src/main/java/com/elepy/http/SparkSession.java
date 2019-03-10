package com.elepy.http;

import javax.servlet.http.HttpSession;
import java.util.Set;

public class SparkSession implements Session {
    private final spark.Session session;

    public SparkSession(spark.Session session) {
        this.session = session;
    }


    @Override
    public HttpSession raw() {
        return session.raw();
    }

    @Override
    public <T> T attribute(String name) {
        return session.attribute(name);
    }

    @Override
    public void attribute(String name, Object value) {
        session.attribute(name, value);
    }

    @Override
    public Set<String> attributes() {
        return session.attributes();
    }

    @Override
    public long creationTime() {
        return session.creationTime();
    }

    @Override
    public String id() {
        return session.id();
    }

    @Override
    public long lastAccessedTime() {
        return session.lastAccessedTime();
    }

    @Override
    public int maxInactiveInterval() {
        return session.maxInactiveInterval();
    }

    @Override
    public void maxInactiveInterval(int interval) {
        session.maxInactiveInterval(interval);
    }

    @Override
    public void invalidate() {
        session.invalidate();
    }

    @Override
    public boolean isNew() {
        return session.isNew();
    }

    @Override
    public void removeAttribute(String name) {
        session.removeAttribute(name);
    }
}
