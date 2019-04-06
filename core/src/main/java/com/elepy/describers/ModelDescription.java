package com.elepy.describers;

import com.elepy.annotations.RestModel;
import com.elepy.evaluators.ObjectEvaluator;
import com.elepy.exceptions.ElepyConfigException;
import com.elepy.http.ActionType;
import com.elepy.http.HttpAction;
import com.elepy.http.HttpMethod;
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


    private final List<HttpAction> actions;


    public ModelDescription(ResourceDescriber<T> resourceDescriber, String slug, String name, Class<T> modelType, IdentityProvider<T> identityProvider, List<ObjectEvaluator<T>> objectEvaluators) {
        this.modelType = modelType;
        this.identityProvider = identityProvider;
        this.objectEvaluators = objectEvaluators;
        this.slug = slug;
        this.name = name;
        this.resourceDescriber = resourceDescriber;
        this.actions = new ArrayList<>();
        this.jsonDescription = generateJsonDescription();

        this.restModelAnnotation = resourceDescriber.getClassType().getAnnotation(RestModel.class);


    }


    public RestModel getRestModelAnnotation() {
        return restModelAnnotation;
    }

    public String getDefaultSortField() {
        final String s = restModelAnnotation.defaultSortField();

        if (s.isEmpty()) {
            return ClassUtils.getPropertyName(ClassUtils.getIdField(modelType).orElseThrow(() -> new ElepyConfigException("No ID field found")));
        } else {
            return s;
        }
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

        model.put("defaultActions", getDefaultActions());
        model.put("actions", getActions());
        model.put("slug", this.slug);
        model.put("name", this.name);

        model.put("javaClass", this.modelType.getName());


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

    private List<HttpAction> getDefaultActions() {
        return Arrays.asList(
                HttpAction.of("Find One", getSlug() + "/:id", resourceDescriber.getFindAccessLevel(), HttpMethod.GET, ActionType.SINGLE),
                HttpAction.of("Find Many", getSlug(), resourceDescriber.getFindAccessLevel(), HttpMethod.GET, ActionType.MULTIPLE),
                HttpAction.of("Update One", getSlug() + "/:id", resourceDescriber.getUpdateAccessLevel(), HttpMethod.PUT, ActionType.SINGLE),
                HttpAction.of("Delete One", getSlug() + "/:id", resourceDescriber.getDeleteAccessLevel(), HttpMethod.DELETE, ActionType.SINGLE),
                HttpAction.of("Create One", getSlug(), resourceDescriber.getCreateAccessLevel(), HttpMethod.POST, ActionType.MULTIPLE)
        );
    }


    public List<HttpAction> getActions() {
        return actions;
    }
    @Override
    public int hashCode() {
        return Objects.hash(modelType);
    }
}
