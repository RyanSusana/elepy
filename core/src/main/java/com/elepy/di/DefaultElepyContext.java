package com.elepy.di;

import com.elepy.annotations.Inject;
import com.elepy.dao.Crud;
import com.elepy.exceptions.ElepyConfigException;
import com.elepy.utils.ClassUtils;

import java.util.*;
import java.util.stream.Collectors;

public class DefaultElepyContext implements ElepyContext {

    private final Map<ContextKey, Object> contextMap;

    private UnsatisfiedDependencies unsatisfiedDependencies;

    private List<ContextKey> preInitialisedDependencies;
    private boolean strictMode = false;

    public DefaultElepyContext() {
        this.contextMap = new HashMap<>();
        this.unsatisfiedDependencies = new UnsatisfiedDependencies(this);
        preInitialisedDependencies = new ArrayList<>();
    }

    public <T> void registerDependency(Class<T> cls, T object) {
        registerDependency(cls, null, object);
    }

    public <T> void registerDependency(T object) {
        registerDependency(object, null);
    }

    public <T> void registerDependency(T object, String tag) {
        ContextKey<?> contextKey = new ContextKey<>(object.getClass(), tag);
        ensureUniqueDependency(contextKey);
        contextMap.put(contextKey, object);
        preInitialisedDependencies.add(contextKey);
    }

    public <T> void registerDependency(Class<T> cls, String tag, T object) {
        ContextKey<T> contextKey = new ContextKey<>(cls, tag);
        ensureUniqueDependency(contextKey);
        contextMap.put(contextKey, object);
        preInitialisedDependencies.add(contextKey);
    }

    private <T> void ensureUniqueDependency(ContextKey<T> key) {
        if (contextMap.containsKey(key)) {
            throw new ElepyConfigException(String.format("Elepy already has a key with the class '%s' and the tag '%s'", key.getClassType(), key.getTag()));
        }
    }

    public <T> T getDependency(Class<T> cls, String tag) {


        final ContextKey<T> key;

        if (tag == null && Crud.class.isAssignableFrom(cls)) {
            key = getCrudDependencyKey(cls);
        } else {
            key = new ContextKey<>(cls, tag);

        }
        final T t = (T) contextMap.get(key);
        if (t != null) {
            return t;
        }

        throw new ElepyConfigException(String.format("No context object for %s available with the tag: %s", cls.getName(), tag));
    }

    private <T> ContextKey<T> getCrudDependencyKey(Class<T> cls) {
        List<Map.Entry<ContextKey, Object>> first = contextMap.entrySet().stream().filter(contextKeyObjectEntry ->
                contextKeyObjectEntry.getKey().getClassType().equals(cls)).collect(Collectors.toList());

        if (first.size() == 1) {
            return first.get(0).getKey();
        } else {
            return new ContextKey<>(cls, null);
        }
    }


    public void registerDependency(Class<?> clazz) {
        registerDependency(clazz, ElepyContext.getTag(clazz));
    }

    public void registerDependency(Class<?> clazz, String tag) {
        registerDependency(new ContextKey<>(clazz, tag));
    }

    public void registerDependency(ContextKey contextKey) {
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

        for (ContextKey preInitialisedDependency : preInitialisedDependencies) {
            if (!ClassUtils.searchForFieldsWithAnnotation(preInitialisedDependency.getClassType(), Inject.class).isEmpty()) {
                try {
                    this.injectFields(contextMap.get(preInitialisedDependency));
                } catch (IllegalAccessException ignored) {
                    //Will never be thrown
                }
            }
        }
    }

    public Map<ContextKey, Object> getContextMap() {
        return contextMap;
    }

    @Override
    public Set<ContextKey> getDependencyKeys() {
        return contextMap.keySet();
    }


}
