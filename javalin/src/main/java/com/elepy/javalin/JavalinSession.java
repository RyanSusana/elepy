package com.elepy.javalin;

import com.elepy.http.Session;
import io.javalin.http.Context;

import java.util.Set;

public class JavalinSession implements Session {

    private final Context context;

    public JavalinSession(Context context) {
        this.context = context;
    }

    @Override
    public <T> T attribute(String name) {
        return context.sessionAttribute(name);
    }

    @Override
    public void attribute(String name, Object value) {
        context.sessionAttribute(name, value);
    }

    @Override
    public Set<String> attributes() {
        return context.sessionAttributeMap().keySet();
    }

    @Override
    public String id() {
        return attribute("ID");
    }

}
