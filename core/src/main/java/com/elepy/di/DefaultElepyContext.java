package com.elepy.di;

import com.elepy.annotations.Inject;
import com.elepy.dao.Crud;
import com.elepy.exceptions.ElepyConfigException;
import com.elepy.utils.ReflectionUtils;

import java.util.*;
import java.util.stream.Collectors;

public class DefaultElepyContext implements ElepyContext {

    private final Map<ContextKey, Object> contextMap;

    private final Injector injector;

    private List<ContextKey> preInitialisedDependencies;
    private boolean strictMode = false;
    private DependencyResolver dependencyResolver;

    public DefaultElepyContext() {
        this.contextMap = new HashMap<>();
        this.dependencyResolver = new DependencyResolver(this);
        this.preInitialisedDependencies = new ArrayList<>();
        this.injector = new Injector(this);
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
        if (strictMode) {
            injectPreInitializedDependencies();
        }
    }

    public <T> void registerDependency(Class<T> cls, String tag, T object) {
        ContextKey<T> contextKey = new ContextKey<>(cls, tag);
        ensureUniqueDependency(contextKey);
        contextMap.put(contextKey, object);
        preInitialisedDependencies.add(contextKey);
        if (strictMode) {
            injectPreInitializedDependencies();
        }

    }

    private <T> void ensureUniqueDependency(ContextKey<T> key) {
        if (strictMode && contextMap.containsKey(key)) {
            throw new ElepyConfigException(String.format("Elepy already has a key with the class '%s' and the tag '%s'", key.getType(), key.getTag()));
        }
    }

    public <T> T getDependency(Class<T> cls, String tag) {

        final ContextKey<T> key;

        if (tag == null && Crud.class.isAssignableFrom(cls)) {
            key = getCrudDependencyKey(cls);
        } else {
            key = new ContextKey<>(cls, tag);
        }
        if (contextMap.containsKey(key)) {
            return (T) contextMap.get(key);
        } else {
            if (!strictMode) {
                final var dependency = injector.initializeAndInject(cls);
                contextMap.put(key, dependency);
                return dependency;
            }
        }
        throw new ElepyConfigException(String.format("No context object for %s available with the tag: %s", cls.getName(), tag));
    }


    private <T> ContextKey<T> getCrudDependencyKey(Class<T> cls) {
        List<Map.Entry<ContextKey, Object>> first = contextMap.entrySet().stream().filter(contextKeyObjectEntry ->
                contextKeyObjectEntry.getKey().getType().equals(cls)).collect(Collectors.toList());

        if (first.size() == 1) {
            return first.get(0).getKey();
        } else {
            return new ContextKey<>(cls, null);
        }
    }


    public void registerDependency(Class<?> clazz) {
        registerDependency(clazz, ReflectionUtils.getDependencyTag(clazz));
    }

    public void registerDependency(Class<?> clazz, String tag) {
        registerDependency(new ContextKey<>(clazz, tag));
    }

    public void registerDependency(ContextKey contextKey) {
        dependencyResolver.add(contextKey);
        if (strictMode) {
            resolveDependencies();
        }
    }

    public void strictMode(boolean strictMode) {
        this.strictMode = strictMode;
    }

    public void resolveDependencies() {
        dependencyResolver.tryToSatisfy();
        injectPreInitializedDependencies();
    }

    public void injectFields(Object object) {
        injector.injectFields(object);
    }

    private void injectPreInitializedDependencies() {
        for (ContextKey preInitialisedDependency : preInitialisedDependencies) {
            if (!ReflectionUtils.searchForFieldsWithAnnotation(preInitialisedDependency.getType(), Inject.class).isEmpty()) {
                injector.injectFields(contextMap.get(preInitialisedDependency));
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

    @Override
    public <T> T initializeElepyObject(Class<? extends T> cls) {
        return injector.initializeAndInject(cls);
    }


}
