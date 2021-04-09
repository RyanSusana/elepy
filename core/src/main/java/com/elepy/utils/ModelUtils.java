package com.elepy.utils;

import com.elepy.annotations.*;
import com.elepy.dao.FilterType;
import com.elepy.exceptions.ElepyConfigException;
import com.elepy.http.HttpAction;
import com.elepy.models.FieldType;
import com.elepy.models.InputModel;
import com.elepy.models.Property;
import com.elepy.models.Schema;
import com.elepy.models.options.*;

import javax.persistence.Column;
import java.lang.reflect.AccessibleObject;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Field;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ModelUtils {

    private ModelUtils() {
    }

    /**
     * Creates the basics of a schema. This method does not revoke any recursive methods
     */
    public static <T> Schema<T> createShallowSchema(Class<T> classType) {
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
    public static <T> Schema<T> createDeepSchema(Class<T> classType) {

        final var schema = createShallowSchema(classType);

        setupActions(schema);

        schema.setProperties(ModelUtils.describeClass(classType));

        return schema;
    }

    /**
     * Gets a list of properties from a class
     */
    public static List<Property> describeClass(Class cls) {
        return getAccessibleObjects(cls).stream()
                .map(ModelUtils::describeAccessibleObject)
                .sorted(Comparator.naturalOrder())
                .collect(Collectors.toList());
    }

    /**
     * Gets all fields that are not marked as hidden and all methods annotated with @Generated
     */
    public static List<AccessibleObject> getAccessibleObjects(Class<?> cls) {
        return Stream.concat(
                ReflectionUtils.getAllFields(cls).stream()
                        .filter(field -> !field.isAnnotationPresent(Hidden.class)),
                Stream.of(cls.getDeclaredMethods())
                        .filter(method -> method.isAnnotationPresent(Generated.class))
        ).collect(Collectors.toList());
    }

    /**
     * Transforms an AccessibleObject into a property
     */
    public static Property describeAccessibleObject(AccessibleObject accessibleObject) {

        final boolean idProperty;

        if (accessibleObject instanceof Field) {
            idProperty = ReflectionUtils.getIdField(((Field) accessibleObject).getDeclaringClass()).map(field1 -> field1.getName().equals(((Field) accessibleObject).getName())).orElse(false);
        } else {
            idProperty = false;
        }

        Property property = createProperty(accessibleObject, idProperty);

        setupSearch(accessibleObject, property, idProperty);
        return property;
    }

    private static Property createProperty(AccessibleObject accessibleObject, boolean idProperty) {
        Property property = createTypedProperty(accessibleObject);

        setupPropertyBasics(accessibleObject, idProperty, property);

        return property;
    }


    public static void setupPropertyBasics(AccessibleObject accessibleObject, boolean idProperty, Property property) {
        final Column column = Annotations.get(accessibleObject, Column.class);
        final Importance importance = Annotations.get(accessibleObject, Importance.class);
        final Description description = Annotations.get(accessibleObject, Description.class);
        property.setHiddenFromCMS(accessibleObject.isAnnotationPresent(Hidden.class));

        property.setName(ReflectionUtils.getPropertyName(accessibleObject));
        property.setDescription(Optional.ofNullable(description).map(Description::value).orElse(null));
        property.setShowIf(Optional.ofNullable(accessibleObject.getAnnotation(ShowIf.class)).map(ShowIf::value).orElse("true"));
        property.setJavaName(ReflectionUtils.getJavaName(accessibleObject));
        property.setLabel(ReflectionUtils.getLabel(accessibleObject));
        property.setEditable(!idProperty && (!accessibleObject.isAnnotationPresent(Uneditable.class) || (column != null && !column.updatable())));
        property.setImportance(importance == null ? 0 : importance.value());
        property.setUnique(idProperty || accessibleObject.isAnnotationPresent(Unique.class) || (column != null && column.unique()));
        property.setGenerated(accessibleObject.isAnnotationPresent(Generated.class) || (idProperty && !accessibleObject.isAnnotationPresent(Identifier.class)) || (idProperty && accessibleObject.isAnnotationPresent(Identifier.class) && Annotations.get(accessibleObject, Identifier.class).generated()));
    }

    private static void setupSearch(AccessibleObject accessibleObject, Property property, boolean idProperty) {
        property.setSearchable(accessibleObject.isAnnotationPresent(Searchable.class) || idProperty);
        Set<Map<String, Object>> availableFilters = FilterType.getForFieldType(property.getType()).stream().map(FilterType::toMap).collect(Collectors.toSet());

        property.setAvailableFilters(availableFilters);
    }


    private static <T> void setupActions(Schema<T> schema) {

        schema.getDefaultActions().put("delete", DefaultActions.getDeleteFromSchema(schema));
        schema.getDefaultActions().put("update", DefaultActions.getUpdateFromSchema(schema));
        schema.getDefaultActions().put("find", DefaultActions.getFindOneFromSchema(schema));
        schema.getDefaultActions().put("create", DefaultActions.getCreateFromSchema(schema));

        schema.setActions(Stream.of(schema.getJavaClass().getAnnotationsByType(Action.class))
                .map(actionAnnotation -> actionToHttpAction(schema.getPath(), actionAnnotation))
                .collect(Collectors.toList()));
    }

    public static HttpAction actionToHttpAction(String modelPath, Action actionAnnotation) {
        final String multiPath = modelPath + "/actions" + (actionAnnotation.path().isEmpty() ? "/" + StringUtils.slugify(actionAnnotation.name()) : actionAnnotation.path());

        InputModel inputModel = null;

        final Class<?> inputClass = actionAnnotation.input();


        if (!inputClass.equals(Object.class)) {
            final var properties = describeClass(inputClass);
            if (!properties.isEmpty()) {
                inputModel = new InputModel();
                inputModel.setProperties(properties);
            }
        }

        return new HttpAction(
                actionAnnotation.name(),
                multiPath,
                actionAnnotation.requiredPermissions(),
                actionAnnotation.method(),
                actionAnnotation.singleRecord(),
                actionAnnotation.multipleRecords(),
                StringUtils.emptyToNull(actionAnnotation.description()),
                StringUtils.emptyToNull(actionAnnotation.warning()),
                inputModel
        );
    }


    private static void setupImportantFields(Schema<?> schema) {

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

    private static Property createTypedProperty(AccessibleObject field) {
        Property property = new Property();
        FieldType fieldType = FieldType.guessFieldType(field);


        property.setType(fieldType);
        property.setOptions(getOptions(field, fieldType));

        return property;
    }

    public static Options getOptions(AnnotatedElement field, FieldType fieldType) {
        switch (fieldType) {
            case INPUT:
                return InputOptions.of(field);
            case DATE:
                return DateOptions.of(field);
            case NUMBER:
                return NumberOptions.of(field);
            case ENUM:
                return EnumOptions.of(field);
            case OBJECT:
                return ObjectOptions.of(field);
            case BOOLEAN:
                return BooleanOptions.of(field);
            case ARRAY:
                return ArrayOptions.of(field);
            case FILE_REFERENCE:
                return FileReferenceOptions.of(field);
            case REFERENCE:
                return ReferenceOptions.of(field);
            case CUSTOM:
                return CustomOptions.of(field);
            case DYNAMIC:
                return DynamicOptions.of(field);
            default:
                return null;

        }
    }


}
