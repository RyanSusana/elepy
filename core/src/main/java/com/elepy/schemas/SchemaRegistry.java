package com.elepy.schemas;

import com.elepy.auth.users.User;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Produces;
import jakarta.enterprise.inject.spi.InjectionPoint;
import jakarta.inject.Inject;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@ApplicationScoped
public class SchemaRegistry {

    private final Map<Class<?>, Schema<?>> schemas = new HashMap<>();

    private final SchemaFactory schemaFactory;

    @Inject
    public SchemaRegistry(SchemaFactory schemaFactory) {
        this.schemaFactory = schemaFactory;
    }

    public boolean hasSchema(Class<?> clazz) {
        return getClassKeyIncludingParentTypes(clazz) != null;
    }

    public void addSchema(Class<?> clazz) {
        schemas.put(clazz, null);
    }

    public <T> Schema<T> getSchema(Class<T> clazz) {
        var key = getClassKeyIncludingParentTypes(clazz);
        if (!schemas.containsKey(key)) {
            throw new IllegalArgumentException("No schema found for class: " + clazz.getName());
        }
        if (schemas.get(key) == null) {
            schemas.put(key, schemaFactory.createDeepSchema(key));
        }
        return (Schema<T>) schemas.get(key);
    }

    @Produces
    public List<Schema> getSchemas() {
        var list = new ArrayList<Schema>();
        schemas.forEach((cls, schemas) -> {
            list.add(getSchema(cls));
        });
        return list;
    }

    @Produces
    public <T> Schema<T> produceSchemaForInjectionPoint(InjectionPoint injectionPoint) {
        ParameterizedType type = (ParameterizedType) injectionPoint.getType();
        Type actualTypeArgument = type.getActualTypeArguments()[0];
        Class<T> genericType = (Class<T>) actualTypeArgument;

        return getSchema(genericType);
    }


    private Class<?> getClassKeyIncludingParentTypes(Class<?> classToGetKey) {
        for (var aClass : schemas.keySet()) {
            if (classToGetKey.isAssignableFrom(aClass)) {
                return aClass;
            }
        }
        return null;
    }

}
