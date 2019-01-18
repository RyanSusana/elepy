package com.elepy.di;

import com.elepy.exceptions.ElepyConfigException;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class DefaultElepyContext implements ElepyContext {

    private final Map<ContextKey, Object> contextMap;

    private UnsatisfiedDependencies unsatisfiedDependencies;

    private boolean strictMode = false;

    public DefaultElepyContext() {
        this.contextMap = new HashMap<>();
        this.unsatisfiedDependencies = new UnsatisfiedDependencies(this);
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

        throw new ElepyConfigException(String.format("No context object for %s available with the tag: %s", cls.getName(), tag));
    }

    public void requireDependency(Class<?> clazz) {
        requireDependency(clazz, ElepyContext.getTag(clazz));
    }

    public void requireDependency(Class<?> clazz, String tag) {
        requireDependency(new ContextKey<>(clazz, tag));
    }

    public void requireDependency(ContextKey contextKey) {
        unsatisfiedDependencies.add(contextKey);
        if (strictMode) {
            resolveDependencies();
        }
    }


    public void strictMode(boolean strictMode) {
        this.strictMode = strictMode;
    }

    public void resolveDependencies() {
        unsatisfiedDependencies.tryToSatisfy();
    }

    public Map<ContextKey, Object> getContextMap() {
        return contextMap;
    }

    @Override
    public Set<ContextKey> getDependencyKeys() {
        return contextMap.keySet();
    }


}
