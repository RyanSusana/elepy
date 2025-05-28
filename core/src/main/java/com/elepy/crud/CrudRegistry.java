package com.elepy.crud;

import com.elepy.annotations.Dao;
import com.elepy.revisions.Revision;
import com.elepy.revisions.RevisionCrud;
import com.elepy.schemas.Schema;
import com.elepy.schemas.SchemaRegistry;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Produces;
import jakarta.enterprise.inject.spi.CDI;
import jakarta.enterprise.inject.spi.InjectionPoint;
import jakarta.inject.Inject;

import java.util.HashMap;
import java.util.Map;

@ApplicationScoped
public class CrudRegistry {
    private Class<? extends CrudFactory> defaultCrudFactoryClass;

    @Inject
    private SchemaRegistry schemaRegistry;

    private Map<Class<?>, Crud<?>> cruds = new HashMap<>();

    @Produces
    public <T> Crud<T> produceCrudForInjectionPoint(InjectionPoint injectionPoint) {
        final Schema<T> schema  = schemaRegistry.produceSchemaForInjectionPoint(injectionPoint);
        return cachedGetCrud(schema);
    }

    public <T> Crud<T> getCrudFor(Class<T> clazz) {
        final var schema = schemaRegistry.getSchema(clazz);
        return cachedGetCrud(schema);
    }

    // Done because If the producer method return type is a parameterized type with a type variable, it must have scope @Dependent.
    // If a producer method with a parameterized return type with a type variable declares any scope other than @Dependent,
    // the container automatically detects the problem and treats it as a definition error.
    private <T>Crud<T> cachedGetCrud(Schema<T> schema){
        if (!cruds.containsKey(schema.getJavaClass())) {
            final Class<? extends CrudFactory> crudFactoryClass = defaultCrudFactoryClass;

            CrudFactory crudFactory = CDI.current().select(crudFactoryClass).get();
            var crud = crudFactory.crudFor(schema);
            cruds.put(schema.getJavaClass(), crud);
        }
        return (Crud<T>) cruds.get(schema.getJavaClass());
    }

    public void setDefaultCrudFactoryClass(Class<? extends CrudFactory> defaultCrudFactoryClass) {
        this.defaultCrudFactoryClass = defaultCrudFactoryClass;
    }
}
