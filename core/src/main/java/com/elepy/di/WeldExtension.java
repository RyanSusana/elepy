package com.elepy.di;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.Observes;
import jakarta.enterprise.inject.spi.*;

import java.util.*;
import java.util.function.Supplier;

public class WeldExtension implements Extension {
    private Map<Class<?>, Object> dependenciesObjects = new HashMap<>();
    private Map<Class<?>, Supplier> dependencySuppliers = new HashMap<>();
    private Set<Class<?>> dependencyClasses = new HashSet<>();



    public void registerUninitializedDependencies(@Observes BeforeBeanDiscovery bbdEvent) {
        dependencyClasses.forEach(cls -> {
            bbdEvent.addAnnotatedType(cls, cls.getName());
        });
    }

    public void registerInitializedDependencies(@Observes AfterBeanDiscovery abdEvent) {
        dependenciesObjects.forEach((cls, obj) -> {
            abdEvent.addBean()
                    .types(cls)
                    .scope(ApplicationScoped.class)
                    .name(cls.getName())
                    .beanClass(cls)
                    .createWith(creationalContext -> obj);
        });


        dependencySuppliers.forEach((cls, supplier) -> {
            abdEvent.addBean()
                    .types(cls)
                    .scope(ApplicationScoped.class)
                    .name(cls.getName())
                    .beanClass(cls)
                    .produceWith(x -> {
                        return supplier.get();
                    });
        });

    }


    public <T> void registerDependency(Class<T> cls, T object) {
        this.dependenciesObjects.put(cls, object);
    }


    public <T> void registerDependency(T object) {
        dependenciesObjects.put(object.getClass(), object);
    }


    public <T> void registerDependency(T object, String tag) {
        dependenciesObjects.put(object.getClass(), object);
    }


    public <T> void registerDependencySupplier(Class<T> clazz, String tag, Supplier<? extends T> supplier) {
        dependencySuppliers.put(clazz, supplier);

    }


    public <T> void registerDependency(Class<T> cls, String tag, T object) {
        dependenciesObjects.put(cls, object);
    }


    public void registerDependency(Class<?> clazz) {
        dependencyClasses.add(clazz);
    }


    public void registerDependency(Class<?> clazz, String tag) {
        dependencyClasses.add(clazz);
    }

}
