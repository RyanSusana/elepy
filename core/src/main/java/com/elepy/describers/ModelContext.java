package com.elepy.describers;

import com.elepy.annotations.RestModel;
import com.elepy.evaluators.ObjectEvaluator;
import com.elepy.exceptions.ElepyConfigException;
import com.elepy.http.ActionType;
import com.elepy.http.HttpAction;
import com.elepy.http.HttpMethod;
import com.elepy.id.IdentityProvider;
import com.elepy.utils.ReflectionUtils;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class ModelContext<T> {

    private final Class<T> modelType;
    private final IdentityProvider<T> identityProvider;
    private final List<ObjectEvaluator<T>> objectEvaluators;
    private final ResourceDescriber<T> resourceDescriber;
    private final RestModelDesc jsonDescription;
    private final RestModel restModelAnnotation;

    private final List<HttpAction> actions;
    private final String slug;
    private final String name;


    public ModelContext(ResourceDescriber<T> resourceDescriber, String slug, String name, Class<T> modelType, IdentityProvider<T> identityProvider, List<ObjectEvaluator<T>> objectEvaluators, List<HttpAction> actions) {
        this.modelType = modelType;
        this.identityProvider = identityProvider;
        this.objectEvaluators = objectEvaluators;
        this.slug = slug;
        this.name = name;
        this.resourceDescriber = resourceDescriber;
        this.actions = actions;
        this.jsonDescription = generateJsonDescription(actions, getDefaultActions());

        this.restModelAnnotation = resourceDescriber.getModelType().getAnnotation(RestModel.class);


    }


    public RestModel getRestModelAnnotation() {
        return restModelAnnotation;
    }

    public String getDefaultSortField() {
        final String s = restModelAnnotation.defaultSortField();

        if (s.isEmpty()) {
            return ReflectionUtils.getPropertyName(ReflectionUtils.getIdField(modelType).orElseThrow(() -> new ElepyConfigException("No ID field found")));
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


    public RestModelDesc getJsonDescription() {
        return jsonDescription;
    }

    private RestModelDesc generateJsonDescription(List<HttpAction> actions, List<HttpAction> defaultActions) {
        RestModelDesc model = new RestModelDesc();

        model.setDefaultActions(defaultActions);
        model.setActions(actions);
        model.setSlug(this.slug);
        model.setName(this.name);
        model.setJavaClass(this.modelType.getName());
        model.setIdField(evaluateHasIdField(modelType));
        model.setProperties(StructureDescriber.describeClass(modelType));
        return model;
    }


    private String evaluateHasIdField(Class cls) {

        Field field = ReflectionUtils.getIdField(cls).orElseThrow(() -> new ElepyConfigException(cls.getName() + " doesn't have a valid identifying field, please annotate a String/Long/Int field with @Identifier"));

        if (!Arrays.asList(Long.class, String.class, Integer.class).contains(org.apache.commons.lang3.ClassUtils.primitivesToWrappers(field.getType())[0])) {
            throw new ElepyConfigException(String.format("The id field '%s' is not a Long, String or Int", field.getName()));
        }

        return ReflectionUtils.getPropertyName(field);

    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ModelContext<?> that = (ModelContext<?>) o;
        return modelType.equals(that.modelType);
    }

    private List<HttpAction> getDefaultActions() {
        return Arrays.asList(
                HttpAction.of("Find One", getSlug() + "/:id", resourceDescriber.getFindPermissions(), HttpMethod.GET, ActionType.SINGLE),
                HttpAction.of("Find Many", getSlug(), resourceDescriber.getFindPermissions(), HttpMethod.GET, ActionType.MULTIPLE),
                HttpAction.of("Update", getSlug() + "/:id", resourceDescriber.getUpdatePermissions(), HttpMethod.PUT, ActionType.SINGLE),
                HttpAction.of("Delete", getSlug() + "/:id", resourceDescriber.getDeletePermissions(), HttpMethod.DELETE, ActionType.SINGLE),
                HttpAction.of("Create", getSlug(), resourceDescriber.getCreatePermissions(), HttpMethod.POST, ActionType.MULTIPLE)
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
