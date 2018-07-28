package com.elepy;


import com.elepy.annotations.DaoProvider;
import com.elepy.annotations.Evaluators;
import com.elepy.annotations.RestModel;
import com.elepy.concepts.IdentityProvider;
import com.elepy.concepts.ObjectEvaluator;
import com.elepy.concepts.ObjectEvaluatorImpl;
import com.elepy.models.AccessLevel;
import com.elepy.routes.*;
import com.elepy.utils.ClassUtils;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class ResourceDescriber<T> {

    private final Elepy elepy;
    public Class<T> clazz;
    private RouteHandler<T> deleteImplementation;
    private RouteHandler<T> updateImplementation;
    private RouteHandler<T> findImplementation;
    private RouteHandler<T> createImplementation;

    private IdentityProvider<T> identityProvider;

    private com.elepy.dao.CrudProvider crudProvider;

    private AccessLevel deleteAccessLevel;
    private AccessLevel findAccessLevel;
    private AccessLevel updateAccessLevel;
    private AccessLevel createAccessLevel;

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
        final DaoProvider annotation = clazz.getAnnotation(DaoProvider.class);
        if (annotation == null) {
            crudProvider = ClassUtils.emptyConstructor(elepy.getDefaultCrudProvider()).newInstance();
        } else {
            crudProvider = ClassUtils.emptyConstructor(annotation.value()).newInstance();
        }
    }

    private void setupEvaluators() throws IllegalAccessException, InstantiationException, InvocationTargetException {
        objectEvaluators = new ArrayList<>();

        final Evaluators annotation = clazz.getAnnotation(Evaluators.class);
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
            deleteAccessLevel = AccessLevel.ADMIN;
        } else {
            final Constructor<? extends RouteHandler> constructor = ClassUtils.emptyConstructor(deleteAnnotation.handler());
            deleteImplementation = constructor.newInstance();
            deleteAccessLevel = deleteAnnotation.accessLevel();
        }

        if (updateAnnotation == null) {
            updateAccessLevel = AccessLevel.ADMIN;
            updateImplementation = new DefaultUpdate<>();
        } else {
            final Constructor<? extends RouteHandler> constructor = ClassUtils.emptyConstructor(updateAnnotation.handler());
            updateImplementation = constructor.newInstance();
            updateAccessLevel = updateAnnotation.accessLevel();
        }

        if (findAnnotation == null) {
            findAccessLevel = AccessLevel.PUBLIC;
            findImplementation = new DefaultFind<>();
        } else {
            final Constructor<? extends RouteHandler> constructor = ClassUtils.emptyConstructor(findAnnotation.handler());
            findImplementation = constructor.newInstance();
            findAccessLevel = findAnnotation.accessLevel();
        }

        if (createAnnotation == null) {
            createAccessLevel = AccessLevel.PUBLIC;
            createImplementation = new DefaultCreate<>();
        } else {
            final Constructor<? extends RouteHandler> constructor = ClassUtils.emptyConstructor(createAnnotation.handler());
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

    public RouteHandler<T> getDeleteImplementation() {
        return deleteImplementation;
    }

    public RouteHandler<T> getUpdateImplementation() {
        return updateImplementation;
    }

    public RouteHandler<T> getFindImplementation() {
        return findImplementation;
    }

    public RouteHandler<T> getCreateImplementation() {
        return createImplementation;
    }

    public AccessLevel getDeleteAccessLevel() {
        return deleteAccessLevel;
    }

    public AccessLevel getFindAccessLevel() {
        return findAccessLevel;
    }

    public AccessLevel getUpdateAccessLevel() {
        return updateAccessLevel;
    }

    public AccessLevel getCreateAccessLevel() {
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

    public com.elepy.dao.CrudProvider getCrudProvider() {
        return crudProvider;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ResourceDescriber<?> that = (ResourceDescriber<?>) o;
        return Objects.equals(slug, that.slug);
    }

    public IdentityProvider<T> getIdentityProvider() {
        return identityProvider;
    }

    @Override
    public int hashCode() {

        return Objects.hash(slug);
    }

}
