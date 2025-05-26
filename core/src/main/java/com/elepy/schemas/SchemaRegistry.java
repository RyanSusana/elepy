package com.elepy.schemas;

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

    @Inject
    private SchemaFactory schemaFactory;
    public SchemaRegistry(){
    }

    public void addSchema(Class<?> clazz) {
        schemas.put(clazz, null);
    }

    public <T> Schema<T> getSchema(Class<T> clazz){
        if(!schemas.containsKey(clazz)){
            throw new IllegalArgumentException("No schema found for class: " + clazz.getName());
        }
        if (schemas.get(clazz) == null) {
            schemas.put(clazz, schemaFactory.createDeepSchema(clazz));
        }
        return (Schema<T>) schemas.get(clazz);
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

}
