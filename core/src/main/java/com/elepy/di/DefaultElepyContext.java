package com.elepy.di;

import com.elepy.exceptions.ElepyConfigException;

import java.util.HashMap;
import java.util.Map;

public class DefaultElepyContext implements ElepyContext {

    private final Map<ContextKey, Object> contextMap;

    public DefaultElepyContext() {
        this.contextMap = new HashMap<>();
    }

    public <T> void attachSingleton(Class<T> cls, T object) {
        attachSingleton(cls, null, object);
    }

    public <T> void attachSingleton(T object) {
        attachSingleton(object, null);
    }

    public <T> void attachSingleton(T object, String tag) {
        contextMap.put(new ContextKey<>(object.getClass(), tag), object);
    }

    public <T> void attachSingleton(Class<T> cls, String tag, T object) {
        contextMap.put(new ContextKey<>(cls, tag), object);
    }

    public <T> T getSingleton(Class<T> cls, String tag) {
        final ContextKey<T> key = new ContextKey<>(cls, tag);

        final T t = (T) contextMap.get(key);
        if (t != null) {
            return t;
        }

        throw new ElepyConfigException(String.format("No context object for %s available", cls.getName()));
    }


}
