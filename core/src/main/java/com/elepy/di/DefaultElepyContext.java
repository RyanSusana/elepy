package com.elepy.di;

import com.elepy.annotations.Inject;
import com.elepy.dao.Crud;
import com.elepy.exceptions.ElepyConfigException;
import com.elepy.utils.ReflectionUtils;

import java.util.*;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public class DefaultElepyContext implements ElepyContext {

    private final Map<ContextKey, Object> dependencies;
    private final Map<ContextKey, Supplier> dependencySuppliers;

    private final Injector injector;

    private List<ContextKey> preInitialisedDependencies;
    private boolean strictMode = false;
    private DependencyResolver dependencyResolver;

    public DefaultElepyContext() {
        this.dependencies = new HashMap<>();
        this.dependencySuppliers = new HashMap<>();

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
        dependencies.put(contextKey, object);
        preInitialisedDependencies.add(contextKey);
        if (strictMode) {
            injectPreInitializedDependencies();
        }
    }

    public <T> void registerDependencySupplier(Class<T> clazz, String tag, Supplier<? extends T> supplier) {
        ContextKey<T> contextKey = new ContextKey<>(clazz, tag);
        ensureUniqueDependency(contextKey);
        dependencySuppliers.put(contextKey, supplier);
    }

    public <T> void registerDependency(Class<T> cls, String tag, T object) {
        ContextKey<T> contextKey = new ContextKey<>(cls, tag);
        ensureUniqueDependency(contextKey);
        dependencies.put(contextKey, object);
        preInitialisedDependencies.add(contextKey);
        if (strictMode) {
            injectPreInitializedDependencies();
        }

    }

    private <T> void ensureUniqueDependency(ContextKey<T> key) {
        if (strictMode && (dependencies.containsKey(key) || dependencySuppliers.containsKey(key))) {
            throw new ElepyConfigException(String.format("Elepy already has a key with the class '%s' and the tag '%s'", key.getType(), key.getTag()));
        }
    }

    public <T> T getDependency(Class<T> cls, String tag) {

        final ContextKey<T> key = getKey(cls, tag);

        if (dependencies.containsKey(key)) {
            return (T) dependencies.get(key);
        } else if (dependencySuppliers.containsKey(key)) {
            dependencies.put(key, dependencySuppliers.get(key).get());
            return getDependency(cls, tag);
        } else {
            if (!strictMode) {
                final var dependency = injector.initializeAndInject(cls);
                dependencies.put(key, dependency);
                return dependency;
            }
        }
        throw new ElepyConfigException(String.format("No context object for %s available with the tag: %s", cls.getName(), tag));
    }

    private <T> ContextKey<T> getKey(Class<T> cls, String tag) {
        if (tag == null && Crud.class.isAssignableFrom(cls)) {
            return getCrudKey(cls);
        } else {
            return new ContextKey<>(cls, tag);
        }
    }


    private <T> ContextKey<T> getCrudKey(Class<T> cls) {
        List<Map.Entry<ContextKey, Object>> first = dependencies.entrySet().stream().filter(contextKeyObjectEntry ->
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
                injector.injectFields(dependencies.get(preInitialisedDependency));
            }
        }
    }

    @Override
    public Set<ContextKey> getDependencyKeys() {
        return dependencies.keySet();
    }

    @Override
    public <T> T initialize(Class<? extends T> cls) {
        return injector.initializeAndInject(cls);
    }


}
