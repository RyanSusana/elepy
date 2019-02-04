package com.elepy;


import com.elepy.annotations.Dao;
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

public class ResourceDescriber<T> implements Comparable<ResourceDescriber> {

    private final Elepy elepy;
    private final StructureDescriber structureDescriber;
    private Class<T> classType;
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
        this.classType = clazz;
        this.elepy = elepy;
        this.structureDescriber = new StructureDescriber(this.classType);
        try {
            setupAnnotations();
        } catch (IllegalAccessException | InvocationTargetException | InstantiationException e) {
            throw new ElepyConfigException("Failed to beforeElepyConstruction elepy, while trying to process Reflection");
        }

    }


    private void setupAnnotations() throws IllegalAccessException, InvocationTargetException, InstantiationException {

        baseAnnotations();
        routeAnnotations();
        setupEvaluators();
    }

    private void setupDao(String slug) throws IllegalAccessException, InvocationTargetException, InstantiationException {
        final DaoProvider annotation = classType.getAnnotation(DaoProvider.class);
        final Crud<T> crud;

        if (annotation == null) {
            crudProvider = elepy.initializeElepyObject(elepy.getDefaultCrudProvider());
        } else {
            crudProvider = elepy.initializeElepyObject(annotation.value());
        }

        final Dao daoAnnotation = classType.getAnnotation(Dao.class);
        if (daoAnnotation != null) {
            crud = elepy.initializeElepyObject(daoAnnotation.value());
        } else {
            crud = crudProvider.crudFor(classType);
        }

        elepy.registerDependency(Crud.class, slug, crud);
        elepy.registerDependency(CrudProvider.class, slug, crudProvider);

    }

    private void setupEvaluators() throws IllegalAccessException, InstantiationException, InvocationTargetException {
        objectEvaluators = new ArrayList<>();

        final Evaluators annotation = classType.getAnnotation(Evaluators.class);
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

    private void baseAnnotations() throws IllegalAccessException, InstantiationException, InvocationTargetException {
        final RestModel annotation = classType.getAnnotation(RestModel.class);

        if (annotation == null) {
            throw new ElepyConfigException(String.format("Resources must have the @RestModel Annotation, %s doesn't.", classType.getName()));
        }

        setupDao(annotation.slug());
        this.slug = annotation.slug();
        this.name = annotation.name();
        this.description = annotation.description();
    }


    private void routeAnnotations() throws IllegalAccessException, InvocationTargetException, InstantiationException {

        ServiceBuilder<T> serviceBuilder = new ServiceBuilder<>();

        final com.elepy.annotations.Service serviceAnnotation = classType.getAnnotation(com.elepy.annotations.Service.class);
        final com.elepy.annotations.Delete deleteAnnotation = classType.getAnnotation(com.elepy.annotations.Delete.class);
        final com.elepy.annotations.Update updateAnnotation = classType.getAnnotation(com.elepy.annotations.Update.class);
        final com.elepy.annotations.Find findAnnotation = classType.getAnnotation(com.elepy.annotations.Find.class);
        final com.elepy.annotations.Create createAnnotation = classType.getAnnotation(com.elepy.annotations.Create.class);

        if (serviceAnnotation != null) {
            ServiceHandler<T> initialService = elepy.initializeElepyObject(serviceAnnotation.value());
            elepy.addRouting(ClassUtils.scanForRoutes(initialService));
            serviceBuilder.defaultFunctionality(initialService);
        }
        if (deleteAnnotation != null) {
            deleteAccessLevel = deleteAnnotation.accessLevel();

            if (!deleteAnnotation.handler().equals(DefaultDelete.class)) {
                serviceBuilder.delete(elepy.initializeElepyObject(deleteAnnotation.handler()));
            }
        }

        if (updateAnnotation != null) {
            updateAccessLevel = updateAnnotation.accessLevel();
            if (!updateAnnotation.handler().equals(DefaultUpdate.class)) {
                serviceBuilder.update(elepy.initializeElepyObject(updateAnnotation.handler()));
            }
        }

        if (findAnnotation != null) {
            findAccessLevel = findAnnotation.accessLevel();
            if (!findAnnotation.handler().equals(DefaultFind.class)) {
                serviceBuilder.find(elepy.initializeElepyObject(findAnnotation.handler()));
            }
        }

        if (createAnnotation != null) {
            createAccessLevel = createAnnotation.accessLevel();
            if (!createAnnotation.handler().equals(DefaultCreate.class)) {
                serviceBuilder.create(elepy.initializeElepyObject(createAnnotation.handler()));
            }
        }
        service = serviceBuilder.build();

    }

    public String getSlug() {
        return slug;
    }

    public Class<T> getClassType() {
        return classType;
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

    @Override
    public int compareTo(ResourceDescriber o) {
        return slug.compareTo(o.slug);
    }
}
