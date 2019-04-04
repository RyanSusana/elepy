package com.elepy.describers;

import com.elepy.annotations.RestModel;
import com.elepy.evaluators.ObjectEvaluator;
import com.elepy.exceptions.ElepyConfigException;
import com.elepy.http.AccessLevel;
import com.elepy.id.IdentityProvider;
import com.elepy.utils.ClassUtils;

import java.lang.reflect.Field;
import java.util.*;

public class ModelDescription<T> {

    private final Class<T> modelType;
    private final IdentityProvider<T> identityProvider;
    private final List<ObjectEvaluator<T>> objectEvaluators;
    private final String slug;
    private final String name;
    private final ResourceDescriber<T> resourceDescriber;
    private final Map<String, Object> jsonDescription;
    private final RestModel restModelAnnotation;


    public ModelDescription(ResourceDescriber<T> resourceDescriber, String slug, String name, Class<T> modelType, IdentityProvider<T> identityProvider, List<ObjectEvaluator<T>> objectEvaluators) {
        this.modelType = modelType;
        this.identityProvider = identityProvider;
        this.objectEvaluators = objectEvaluators;
        this.slug = slug;
        this.name = name;
        this.resourceDescriber = resourceDescriber;
        this.jsonDescription = generateJsonDescription();

        this.restModelAnnotation = resourceDescriber.getClassType().getAnnotation(RestModel.class);


    }


    public RestModel getRestModelAnnotation() {
        return restModelAnnotation;
    }

    public Class<T> getModelType() {
        return modelType;
    }

    public IdentityProvider<T> getIdentityProvider() {
        return identityProvider;
    }

    public List<ObjectEvaluator<T>> getObjectEvaluators() {
        return objectEvaluators;
    }

    public String getSlug() {
        return slug;
    }

    public String getName() {
        return name;
    }


    public Map<String, Object> getJsonDescription() {
        return jsonDescription;
    }

    private Map<String, Object> generateJsonDescription() {
        Map<String, Object> model = new HashMap<>();
        model.put("slug", this.slug);
        model.put("name", this.name);

        model.put("javaClass", this.modelType.getName());

        model.put("actions", getActions());
        model.put("idField", evaluateHasIdField(modelType));
        model.put("fields", new ClassDescriber(modelType).getStructure());
        return model;
    }


    private String evaluateHasIdField(Class cls) {

        Field field = ClassUtils.getIdField(cls).orElseThrow(() -> new ElepyConfigException(cls.getName() + " doesn't have a valid identifying field, please annotate a String/Long/Int field with @Identifier"));

        if (!Arrays.asList(Long.class, String.class, Integer.class).contains(org.apache.commons.lang3.ClassUtils.primitivesToWrappers(field.getType())[0])) {
            throw new ElepyConfigException(String.format("The id field '%s' is not a Long, String or Int", field.getName()));
        }

        return ClassUtils.getPropertyName(field);

    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ModelDescription<?> that = (ModelDescription<?>) o;
        return modelType.equals(that.modelType);
    }

    private Map<String, AccessLevel> getActions() {
        Map<String, AccessLevel> actions = new HashMap<>();
        actions.put("findOne", resourceDescriber.getFindAccessLevel());
        actions.put("findAll", resourceDescriber.getFindAccessLevel());
        actions.put("update", resourceDescriber.getUpdateAccessLevel());
        actions.put("delete", resourceDescriber.getDeleteAccessLevel());
        actions.put("create", resourceDescriber.getCreateAccessLevel());
        return actions;
    }

    @Override
    public int hashCode() {
        return Objects.hash(modelType);
    }
}
