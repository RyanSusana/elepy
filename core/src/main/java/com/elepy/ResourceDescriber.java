package com.elepy;


import com.elepy.annotations.Crud;
import com.elepy.annotations.ObjectEvaluators;
import com.elepy.annotations.RestModel;
import com.elepy.concepts.IdProvider;
import com.elepy.concepts.ObjectEvaluator;
import com.elepy.concepts.ObjectEvaluatorImpl;
import com.elepy.dao.CrudProvider;
import com.elepy.dao.MongoProvider;
import com.elepy.models.RestModelAccessType;
import com.elepy.routes.*;
import com.elepy.utils.ClassUtils;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class ResourceDescriber<T> {

    public Class<T> clazz;


    private final Elepy elepy;
    private Delete<T> deleteImplementation;
    private Update<T> updateImplementation;
    private Find<T> findImplementation;
    private Create<T> createImplementation;

    private IdProvider<T> idProvider;

    private CrudProvider<T> crudProvider;

    private RestModelAccessType deleteAccessLevel;
    private RestModelAccessType findAccessLevel;
    private RestModelAccessType updateAccessLevel;
    private RestModelAccessType createAccessLevel;

    private List<ObjectEvaluator<T>> objectEvaluators;
    private String slug;
    private String description;
    private String name;

    public ResourceDescriber(Elepy elepy, Class<T> clazz) {
        this.clazz = clazz;
        this.elepy = elepy;
        try {
            setupAnnotations();
        } catch (IllegalAccessException | InvocationTargetException | InstantiationException e) {
            throw new RuntimeException(e);
        }

    }


    private void setupAnnotations() throws IllegalAccessException, InvocationTargetException, InstantiationException {


        setupDao();
        routeAnnotations();
        baseAnnotations();
        setupEvaluators();


    }

    private void setupDao() throws IllegalAccessException, InvocationTargetException, InstantiationException {
        final Crud annotation = clazz.getAnnotation(Crud.class);
        if (annotation == null) {
            crudProvider = new MongoProvider<>();
        } else {
            crudProvider = ClassUtils.emptyConstructor(annotation.crudProvider()).newInstance();
        }
        crudProvider.setElepy(elepy);
    }

    private void setupEvaluators() throws IllegalAccessException, InstantiationException, InvocationTargetException {
        objectEvaluators = new ArrayList<>();

        final ObjectEvaluators annotation = clazz.getAnnotation(ObjectEvaluators.class);
        objectEvaluators.add(new ObjectEvaluatorImpl<>());

        if (annotation != null) {
            for (Class<? extends ObjectEvaluator> clazz : annotation.value()) {
                if (clazz != null) {
                    final Constructor<? extends ObjectEvaluator> constructor = ClassUtils.emptyConstructor(clazz);
                    objectEvaluators.add(constructor.newInstance());
                }
            }
        }
    }

    private void baseAnnotations() {
        final RestModel annotation = clazz.getAnnotation(RestModel.class);

        if (annotation == null) {
            throw new IllegalStateException("Resources must have the @RestModel Annotation");
        }

        this.slug = annotation.slug();
        this.name = annotation.name();
        this.description = annotation.description();
    }

    private void routeAnnotations() throws IllegalAccessException, InvocationTargetException, InstantiationException {
        final com.elepy.annotations.Delete deleteAnnotation = clazz.getAnnotation(com.elepy.annotations.Delete.class);
        final com.elepy.annotations.Update updateAnnotation = clazz.getAnnotation(com.elepy.annotations.Update.class);
        final com.elepy.annotations.Find findAnnotation = clazz.getAnnotation(com.elepy.annotations.Find.class);
        final com.elepy.annotations.Create createAnnotation = clazz.getAnnotation(com.elepy.annotations.Create.class);


        if (deleteAnnotation == null) {
            deleteImplementation = new DefaultDelete<>();
            deleteAccessLevel = RestModelAccessType.ADMIN;
        } else {
            final Constructor<? extends Delete> constructor = ClassUtils.emptyConstructor(deleteAnnotation.implementation());
            deleteImplementation = constructor.newInstance();
            deleteAccessLevel = deleteAnnotation.accessLevel();
        }

        if (updateAnnotation == null) {
            updateAccessLevel = RestModelAccessType.ADMIN;
            updateImplementation = new DefaultUpdate<>();
        } else {
            final Constructor<? extends Update> constructor = ClassUtils.emptyConstructor(updateAnnotation.implementation());
            updateImplementation = constructor.newInstance();
            updateAccessLevel = updateAnnotation.accessLevel();
        }

        if (findAnnotation == null) {
            findAccessLevel = RestModelAccessType.PUBLIC;
            findImplementation = new DefaultFind<>();
        } else {
            final Constructor<? extends Find> constructor = ClassUtils.emptyConstructor(findAnnotation.implementation());
            findImplementation = constructor.newInstance();
            findAccessLevel = findAnnotation.accessLevel();
        }

        if (createAnnotation == null) {
            createAccessLevel = RestModelAccessType.PUBLIC;
            createImplementation = new DefaultCreate<>();
        } else {
            final Constructor<? extends Create> constructor = ClassUtils.emptyConstructor(createAnnotation.implementation());
            createImplementation = constructor.newInstance();
            createAccessLevel = createAnnotation.accessLevel();
        }

    }

    public String getSlug() {
        return slug;
    }

    public Class<T> getClazz() {
        return clazz;
    }

    public Delete<T> getDeleteImplementation() {
        return deleteImplementation;
    }

    public Update<T> getUpdateImplementation() {
        return updateImplementation;
    }

    public Find<T> getFindImplementation() {
        return findImplementation;
    }

    public Create<T> getCreateImplementation() {
        return createImplementation;
    }

    public RestModelAccessType getDeleteAccessLevel() {
        return deleteAccessLevel;
    }

    public RestModelAccessType getFindAccessLevel() {
        return findAccessLevel;
    }

    public RestModelAccessType getUpdateAccessLevel() {
        return updateAccessLevel;
    }

    public RestModelAccessType getCreateAccessLevel() {
        return createAccessLevel;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public List<ObjectEvaluator<T>> getObjectEvaluators() {
        return objectEvaluators;
    }

    public CrudProvider<T> getCrudProvider() {
        return crudProvider;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ResourceDescriber<?> that = (ResourceDescriber<?>) o;
        return Objects.equals(slug, that.slug);
    }

    public IdProvider<T> getIdProvider() {
        return idProvider;
    }

    @Override
    public int hashCode() {

        return Objects.hash(slug);
    }

}
