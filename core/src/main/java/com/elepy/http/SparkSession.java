package com.elepy.http;

import java.util.Set;

public class SparkSession implements Session {
    private final spark.Session session;

    public SparkSession(spark.Session session) {
        this.session = session;
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
    public String id() {
        return session.id();
    }

}
