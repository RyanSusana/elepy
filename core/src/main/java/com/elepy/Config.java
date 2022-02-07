package com.elepy;

import com.elepy.dao.Crud;
import com.elepy.dao.CrudFactory;
import com.elepy.models.Schema;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Produces;
import jakarta.enterprise.inject.spi.InjectionPoint;
import jakarta.inject.Inject;

import java.lang.reflect.*;

@ApplicationScoped
public class Config {


    @Produces
    public <T> Crud<T> getCrud(InjectionPoint ip, Schemas schemas, CrudFactory crudFactory) {

        var member = toParameterizedType((AnnotatedElement) ip.getMember());


        Schema<T> schema = (Schema<T>) schemas.getSchema((Class<?>) member.getActualTypeArguments()[0]);

        return crudFactory.crudFor(schema);
    }

    private static ParameterizedType toParameterizedType(AnnotatedElement annotatedElement) {
        if (annotatedElement instanceof Parameter) {
            return (ParameterizedType) ((Parameter) annotatedElement).getParameterizedType();
        } else {
            final var annotatedElement1 = (Field) annotatedElement;

            return (ParameterizedType) annotatedElement1.getGenericType();
        }
    }

}
