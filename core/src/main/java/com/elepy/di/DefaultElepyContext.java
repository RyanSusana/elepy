package com.elepy.di;

import com.elepy.annotations.Inject;
import com.elepy.exceptions.ElepyConfigException;
import com.elepy.utils.ReflectionUtils;

import java.util.*;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public class DefaultElepyContext implements ElepyContext {

    private final Map<ContextKey, Object> dependencies;
    private final Map<ContextKey, Supplier> dependencySuppliers;

    private final List<ContextKey> preInitialisedDependencies;
    private boolean strictMode = false;

    private final Resolver resolver;
    private final Injector injector;


    public DefaultElepyContext() {
        this.dependencies = new HashMap<>();
        this.dependencySuppliers = new HashMap<>();

        this.preInitialisedDependencies = new ArrayList<>();

        this.resolver = new Resolver();
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
        ensureDependencyDoesntAlreadyExist(contextKey);
        dependencies.put(contextKey, object);
        preInitialisedDependencies.add(contextKey);
        if (strictMode) {
            injectPreInitializedDependencies();
        }
    }

    public <T> void registerDependencySupplier(Class<T> clazz, String tag, Supplier<? extends T> supplier) {
        ContextKey<T> contextKey = new ContextKey<>(clazz, tag);
        ensureDependencyDoesntAlreadyExist(contextKey);
        dependencySuppliers.put(contextKey, supplier);
    }

    public <T> void registerDependency(Class<T> cls, String tag, T object) {
        ContextKey<T> contextKey = new ContextKey<>(cls, tag);
        ensureDependencyDoesntAlreadyExist(contextKey);
        dependencies.put(contextKey, object);
        preInitialisedDependencies.add(contextKey);
        if (strictMode) {
            injectPreInitializedDependencies();
        }

    }


    public <T> T getDependency(Class<T> cls, String tag) {

        final ContextKey<T> key = new ContextKey<>(cls, tag);

        if (key.getType().equals(ElepyContext.class)) {
            return (T) this;
        } else if (dependencies.containsKey(key)) {
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




    public void registerDependency(Class<?> clazz) {
        registerDependency(clazz, (String) null);
    }

    public void registerDependency(Class<?> clazz, String tag) {
        registerDependency(new ContextKey<>(clazz, tag));
    }

    public void registerDependency(ContextKey contextKey) {
        resolver.addUnsatisfiedDependency(contextKey);
        if (strictMode) {
            resolveDependencies();
        }
    }

    public Set<ContextKey> getUnsatisfiedDependencies() {
        return dependencies.entrySet().stream()
                .filter(contextKeyObjectEntry -> Objects.isNull(contextKeyObjectEntry.getValue()))
                .map(Map.Entry::getKey)
                .collect(Collectors.toSet());
    }

    public Set<ContextKey> getSatisfiedDependencies() {
        return dependencies.entrySet().stream()
                .filter(contextKeyObjectEntry -> Objects.nonNull(contextKeyObjectEntry.getValue()))
                .map(Map.Entry::getKey)
                .collect(Collectors.toSet());
    }

    public void strictMode(boolean strictMode) {
        this.strictMode = strictMode;
    }

    public void resolveDependencies() {
        resolver.resolve(this);
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

    private <T> void ensureDependencyDoesntAlreadyExist(ContextKey<T> key) {
        if (strictMode && (dependencies.containsKey(key) || dependencySuppliers.containsKey(key))) {
            throw new ElepyConfigException(String.format("Elepy already has a key with the class '%s' and the tag '%s'", key.getType(), key.getTag()));
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