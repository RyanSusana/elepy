package com.elepy;


import com.elepy.annotations.DaoProvider;
import com.elepy.annotations.Evaluators;
import com.elepy.annotations.RestModel;
import com.elepy.concepts.IdentityProvider;
import com.elepy.concepts.ObjectEvaluator;
import com.elepy.concepts.ObjectEvaluatorImpl;
import com.elepy.concepts.describers.StructureDescriber;
import com.elepy.dao.Crud;
import com.elepy.dao.CrudProvider;
import com.elepy.exceptions.ElepyConfigException;
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
    private final StructureDescriber structureDescriber;
    private Class<T> clazz;
    private IdentityProvider<T> identityProvider;
    private com.elepy.dao.CrudProvider crudProvider;
    private AccessLevel deleteAccessLevel = AccessLevel.ADMIN;
    private AccessLevel findAccessLevel = AccessLevel.PUBLIC;
    private AccessLevel updateAccessLevel = AccessLevel.ADMIN;
    private AccessLevel createAccessLevel = AccessLevel.ADMIN;
    private List<ObjectEvaluator<T>> objectEvaluators;
    private String slug;
    private String description;
    private String name;


    private ServiceHandler<T> service;

    public ResourceDescriber(Elepy elepy, Class<T> clazz) {
        this.clazz = clazz;
        this.elepy = elepy;
        this.structureDescriber = new StructureDescriber(this.clazz);
        try {
            setupAnnotations();
        } catch (IllegalAccessException | InvocationTargetException | InstantiationException e) {
            throw new ElepyConfigException("Failed to setup elepy, while trying to process Reflection");
        }

    }


    private void setupAnnotations() throws IllegalAccessException, InvocationTargetException, InstantiationException {
        setupDao();
        baseAnnotations();
        routeAnnotations();
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
            for (Class<? extends ObjectEvaluator> objectEvaluatorClass : annotation.value()) {
                if (objectEvaluatorClass != null) {
                    final Constructor<? extends ObjectEvaluator> constructor = ClassUtils.emptyConstructor(objectEvaluatorClass);
                    objectEvaluators.add(constructor.newInstance());
                }
            }
        }
    }

    private void baseAnnotations() {
        final RestModel annotation = clazz.getAnnotation(RestModel.class);

        if (annotation == null) {
            throw new ElepyConfigException("Resources must have the @RestModel Annotation");
        }

        Crud<T> dao = crudProvider.crudFor(clazz, elepy);

        elepy.attachSingleton(Crud.class, annotation.slug(), dao);
        elepy.attachSingleton(CrudProvider.class, annotation.slug(), crudProvider);

        this.slug = annotation.slug();
        this.name = annotation.name();
        this.description = annotation.description();
    }


    private void routeAnnotations() throws IllegalAccessException, InvocationTargetException, InstantiationException {

        ServiceBuilder<T> serviceBuilder = new ServiceBuilder<>();

        final com.elepy.annotations.Service serviceAnnotation = clazz.getAnnotation(com.elepy.annotations.Service.class);
        final com.elepy.annotations.Delete deleteAnnotation = clazz.getAnnotation(com.elepy.annotations.Delete.class);
        final com.elepy.annotations.Update updateAnnotation = clazz.getAnnotation(com.elepy.annotations.Update.class);
        final com.elepy.annotations.Find findAnnotation = clazz.getAnnotation(com.elepy.annotations.Find.class);
        final com.elepy.annotations.Create createAnnotation = clazz.getAnnotation(com.elepy.annotations.Create.class);

        if (serviceAnnotation != null) {
            ServiceHandler<T> initialService = ClassUtils.initializeElepyObject(serviceAnnotation.value(), elepy);
            serviceBuilder.defaultFunctionality(initialService);
        }
        if (deleteAnnotation != null) {
            deleteAccessLevel = deleteAnnotation.accessLevel();

            if (!deleteAnnotation.handler().equals(DefaultDelete.class)) {
                serviceBuilder.delete(ClassUtils.initializeElepyObject(deleteAnnotation.handler(), elepy));
            }
        }

        if (updateAnnotation != null) {
            updateAccessLevel = updateAnnotation.accessLevel();
            if (!updateAnnotation.handler().equals(DefaultUpdate.class)) {
                serviceBuilder.update(ClassUtils.initializeElepyObject(updateAnnotation.handler(), elepy));
            }
        }

        if (findAnnotation != null) {
            findAccessLevel = findAnnotation.accessLevel();
            if (!findAnnotation.handler().equals(DefaultFind.class)) {
                serviceBuilder.find(ClassUtils.initializeElepyObject(findAnnotation.handler(), elepy));
            }
        }

        if (createAnnotation != null) {
            createAccessLevel = createAnnotation.accessLevel();
            if (!createAnnotation.handler().equals(DefaultCreate.class)) {
                serviceBuilder.create(ClassUtils.initializeElepyObject(createAnnotation.handler(), elepy));
            }
        }
        service = serviceBuilder.build();

    }

    public String getSlug() {
        return slug;
    }

    public Class<T> getClazz() {
        return clazz;
    }


    public ServiceHandler<T> getService() {
        return service;
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

    public StructureDescriber getStructureDescriber() {
        return structureDescriber;
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
