package com.elepy.utils;

import com.elepy.annotations.*;
import com.elepy.auth.Permissions;
import com.elepy.dao.FilterType;
import com.elepy.exceptions.ElepyConfigException;
import com.elepy.http.ActionType;
import com.elepy.http.HttpAction;
import com.elepy.http.HttpMethod;
import com.elepy.models.FieldType;
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

    public static List<Property> describeClass(Class cls) {
        return getDeclaredProperties(cls).stream()
                .map(ModelUtils::describeFieldOrMethod)
                .sorted(Comparator.naturalOrder())
                .collect(Collectors.toList());
    }

    public static List<AccessibleObject> getDeclaredProperties(Class<?> cls) {
        return Stream.concat(
                ReflectionUtils.getAllFields(cls).stream()
                        .filter(field -> !field.isAnnotationPresent(Hidden.class)),
                Stream.of(cls.getDeclaredMethods())
                        .filter(method -> method.isAnnotationPresent(Generated.class))
        ).collect(Collectors.toList());
    }

    public static Property describeFieldOrMethod(AccessibleObject accessibleObject) {

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
        final Column column = accessibleObject.getAnnotation(Column.class);
        final Importance importance = accessibleObject.getAnnotation(Importance.class);

        property.setHiddenFromCMS(accessibleObject.isAnnotationPresent(Hidden.class));
        property.setName(ReflectionUtils.getPropertyName(accessibleObject));
        property.setPrettyName(ReflectionUtils.getPrettyName(accessibleObject));
        property.setRequired(accessibleObject.getAnnotation(Required.class) != null);
        property.setEditable(!idProperty && (!accessibleObject.isAnnotationPresent(Uneditable.class) || (column != null && !column.updatable())));
        property.setImportance(importance == null ? 0 : importance.value());
        property.setUnique(idProperty || accessibleObject.isAnnotationPresent(Unique.class) || (column != null && column.unique()));
        property.setGenerated(accessibleObject.isAnnotationPresent(Generated.class) || (idProperty && !accessibleObject.isAnnotationPresent(Identifier.class)) || (idProperty && accessibleObject.isAnnotationPresent(Identifier.class) && accessibleObject.getAnnotation(Identifier.class).generated()));
    }

    private static void setupSearch(AccessibleObject accessibleObject, Property property, boolean idProperty) {
        property.setSearchable(accessibleObject.isAnnotationPresent(Searchable.class) || idProperty);
        Set<Map<String, String>> availableFilters = FilterType.getForFieldType(property.getType()).stream().map(FilterType::toMap).collect(Collectors.toSet());

        property.setAvailableFilters(availableFilters);
    }

    public static <T> Schema<T> createBasicSchema(Class<T> classType) {
        var model = new Schema<T>();
        final Model restModel = classType.getAnnotation(Model.class);


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

        model.setView(Optional.ofNullable(classType.getAnnotation(View.class)).map(View::value).orElse(View.Defaults.DEFAULT));


        setupDefaultActions(model);
        setupImportantFields(model);
        setupActions(model);

        final String toGet = restModel.defaultSortField();
        final String idProperty = model.getIdProperty();
        model.setDefaultSortField(StringUtils.getOrDefault(toGet, idProperty));

        return model;

    }

    public static <T> Schema<T> createSchemaFromClass(Class<T> classType) {

        final var basicSchema = createBasicSchema(classType);

        basicSchema.setProperties(ModelUtils.describeClass(classType));

        return basicSchema;
    }


    private static <T> void setupActions(Schema<T> schema) {
        schema.setActions(Stream.of(schema.getJavaClass().getAnnotationsByType(Action.class))
                .map(actionAnnotation ->
                        actionToHttpAction(schema.getPath(), actionAnnotation))
                .collect(Collectors.toList()));
    }

    public static HttpAction actionToHttpAction(String modelPath, Action actionAnnotation) {
        final String multiPath = modelPath + "/actions" + (actionAnnotation.path().isEmpty() ? "/" + StringUtils.slugify(actionAnnotation.name()) : actionAnnotation.path());
        return HttpAction.of(actionAnnotation.name(), multiPath, actionAnnotation.requiredPermissions(), actionAnnotation.method(), actionAnnotation.actionType());
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
            default:
                return null;

        }
    }

    private static void setupDefaultActions(Schema<?> schema) {

        var createPermissions = Optional
                .ofNullable(schema.getJavaClass().getAnnotation(Create.class))
                .map(Create::requiredPermissions)
                .orElse(Permissions.DEFAULT);

        var updatePermissions = Optional
                .ofNullable(schema.getJavaClass().getAnnotation(Update.class))
                .map(Update::requiredPermissions)
                .orElse(Permissions.DEFAULT);

        var deletePermissions = Optional
                .ofNullable(schema.getJavaClass().getAnnotation(Delete.class))
                .map(Delete::requiredPermissions)
                .orElse(Permissions.DEFAULT);

        var findPermissions = Optional
                .ofNullable(schema.getJavaClass().getAnnotation(Find.class))
                .map(Find::requiredPermissions)
                .orElse(Permissions.NONE);


        schema.setFindOneAction(HttpAction.of("Find One", schema.getPath() + "/:id", findPermissions, HttpMethod.GET, ActionType.SINGLE));
        schema.setFindManyAction(HttpAction.of("Find Many", schema.getPath(), findPermissions, HttpMethod.GET, ActionType.MULTIPLE));
        schema.setUpdateAction(HttpAction.of("Update", schema.getPath() + "/:id", updatePermissions, HttpMethod.PUT, ActionType.SINGLE));
        schema.setDeleteAction(HttpAction.of("Delete", schema.getPath() + "/:id", deletePermissions, HttpMethod.DELETE, ActionType.SINGLE));
        schema.setCreateAction(HttpAction.of("Create", schema.getPath(), createPermissions, HttpMethod.POST, ActionType.MULTIPLE));

    }

}
