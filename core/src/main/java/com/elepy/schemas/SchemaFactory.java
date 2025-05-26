package com.elepy.schemas;

import com.elepy.annotations.*;
import com.elepy.exceptions.ElepyConfigException;
import com.elepy.utils.Annotations;
import com.elepy.utils.ReflectionUtils;
import com.elepy.utils.StringUtils;
import jakarta.enterprise.context.ApplicationScoped;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@ApplicationScoped
public class SchemaFactory {

    private final PropertyFactory propertyFactory = new PropertyFactory();
    private final ActionFactory actionFactory = new ActionFactory();
    /**
     * Creates the basics of a schema. This method does not revoke any recursive methods
     */
    public <T> Schema<T> createShallowSchema(Class<T> classType) {
        var model = new Schema<T>();
        final Model restModel = Annotations.get(classType, Model.class);


        if (restModel == null) {
            throw new ElepyConfigException(String.format(
                    "%s is not annotated with @Model", classType.getName()
            ));
        }

        model.setViewableOnCMS(!classType.isAnnotationPresent(Hidden.class));
        model.setPath(restModel.path());
        model.setName(restModel.name());
        model.setJavaClass(classType);
        model.setDefaultSortDirection(restModel.defaultSortDirection());

        model.setView(Optional.ofNullable(Annotations.get(classType, View.class)).map(View::value).orElse(View.Defaults.DEFAULT));


        setupImportantFields(model);

        final String toGet = restModel.defaultSortField();
        final String idProperty = model.getIdProperty();
        model.setDefaultSortField(StringUtils.getOrDefault(toGet, idProperty));
        return model;
    }


    /**
     * Creates a full schema.
     */
    public <T> Schema<T> createDeepSchema(Class<T> classType) {

        final var schema = createShallowSchema(classType);

        setupActions(schema);

        schema.setProperties(propertyFactory.describeClass(classType));

        return schema;
    }


    private <T> void setupActions(Schema<T> schema) {

        schema.getDefaultActions().put("delete", DefaultActions.getDeleteFromSchema(schema));
        schema.getDefaultActions().put("update", DefaultActions.getUpdateFromSchema(schema));
        schema.getDefaultActions().put("find", DefaultActions.getFindOneFromSchema(schema));
        schema.getDefaultActions().put("create", DefaultActions.getCreateFromSchema(schema));

        schema.setActions(Stream.of(schema.getJavaClass().getAnnotationsByType(Action.class))
                .map(actionAnnotation -> actionFactory.actionToHttpAction(schema.getPath(), actionAnnotation))
                .collect(Collectors.toList()));
    }

    private void setupImportantFields(Schema<?> schema) {
        var cls = schema.getJavaClass();

        Field field = ReflectionUtils.getIdField(cls).orElseThrow(() -> new ElepyConfigException(cls.getName() + " doesn't have a valid identifying field, please annotate a String/Long/Int field with @Identifier"));

        if (!Arrays.asList(Long.class, String.class, Integer.class).contains(org.apache.commons.lang3.ClassUtils.primitivesToWrappers(field.getType())[0])) {
            throw new ElepyConfigException(String.format("The id field '%s' is not a Long, String or Int", field.getName()));
        }

        schema.setIdProperty(ReflectionUtils.getPropertyName(field));

        schema.setFeaturedProperty(ReflectionUtils.searchForFieldWithAnnotation(cls, Featured.class)
                .map(ReflectionUtils::getPropertyName)
                .orElse(schema.getIdProperty()));

    }
}
