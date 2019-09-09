package com.elepy.utils;

import com.elepy.annotations.*;
import com.elepy.auth.Permissions;
import com.elepy.dao.FilterType;
import com.elepy.exceptions.ElepyConfigException;
import com.elepy.http.ActionType;
import com.elepy.http.HttpAction;
import com.elepy.http.HttpMethod;
import com.elepy.models.FieldType;
import com.elepy.models.Model;
import com.elepy.models.Property;
import com.elepy.models.options.*;

import javax.persistence.Column;
import java.lang.reflect.AccessibleObject;
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

    public static List<AccessibleObject> getDeclaredProperties(Class cls) {
        return Stream.concat(
                Stream.of(cls.getDeclaredFields()),
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

    public static Property createProperty(AccessibleObject accessibleObject, boolean idProperty) {
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
        property.setUnique(accessibleObject.isAnnotationPresent(Unique.class) || (column != null && column.unique()));
        property.setGenerated(accessibleObject.isAnnotationPresent(Generated.class) || (idProperty && !accessibleObject.isAnnotationPresent(Identifier.class)) || (idProperty && accessibleObject.isAnnotationPresent(Identifier.class) && accessibleObject.getAnnotation(Identifier.class).generated()));
    }

    private static void setupSearch(AccessibleObject accessibleObject, Property property, boolean idProperty) {
        property.setSearchable(accessibleObject.isAnnotationPresent(Searchable.class) || idProperty);
        Set<Map<String, String>> availableFilters = FilterType.getForFieldType(property.getType()).stream().map(FilterType::toMap).collect(Collectors.toSet());

        //property.setExtra("availableFilters", availableFilters);
    }

    public static <T> Model<T> createModelFromClass(Class<T> classType) {
        var model = new Model<T>();
        final RestModel restModel = classType.getAnnotation(RestModel.class);

        model.setViewableOnCMS(!classType.isAnnotationPresent(Hidden.class));
        model.setSlug(restModel.slug());
        model.setName(restModel.name());
        model.setJavaClass(classType);
        model.setDefaultSortDirection(restModel.defaultSortDirection());
        model.setProperties(ModelUtils.describeClass(classType));


        setupDefaultActions(model);
        setupImportantFields(model);
        setupActions(model);

        final String toGet = restModel.defaultSortField();
        final String idProperty = model.getIdProperty();
        model.setDefaultSortField(StringUtils.getOrDefault(toGet, idProperty));

        return model;
    }


    private static <T> void setupActions(Model<T> model) {
        model.setActions(Stream.of(model.getJavaClass().getAnnotationsByType(Action.class))
                .map(actionAnnotation ->
                        actionToHttpAction(model.getSlug(), actionAnnotation))
                .collect(Collectors.toList()));
    }

    public static HttpAction actionToHttpAction(String modelSlug, Action actionAnnotation) {
        final String multiSlug = modelSlug + "/actions" + (actionAnnotation.slug().isEmpty() ? "/" + StringUtils.slugify(actionAnnotation.name()) : actionAnnotation.slug());
        return HttpAction.of(actionAnnotation.name(), multiSlug, actionAnnotation.requiredPermissions(), actionAnnotation.method(), actionAnnotation.actionType());
    }

    private static void setupImportantFields(Model<?> model) {

        var cls = model.getJavaClass();

        Field field = ReflectionUtils.getIdField(cls).orElseThrow(() -> new ElepyConfigException(cls.getName() + " doesn't have a valid identifying field, please annotate a String/Long/Int field with @Identifier"));

        if (!Arrays.asList(Long.class, String.class, Integer.class).contains(org.apache.commons.lang3.ClassUtils.primitivesToWrappers(field.getType())[0])) {
            throw new ElepyConfigException(String.format("The id field '%s' is not a Long, String or Int", field.getName()));
        }

        model.setIdProperty(ReflectionUtils.getPropertyName(field));

        model.setFeaturedProperty(ReflectionUtils.searchForFieldWithAnnotation(cls, Featured.class)
                .map(ReflectionUtils::getPropertyName)
                .orElse(model.getIdProperty()));

    }

    private static Property createTypedProperty(AccessibleObject field) {
        Property property = new Property();
        FieldType fieldType = FieldType.guessType(field);


        property.setType(fieldType);
        property.setOptions(getOptions(field, fieldType));

        return property;
    }

    private static Options getOptions(AccessibleObject field, FieldType fieldType) {
        switch (fieldType) {
            case TEXT:
                return TextOptions.of(field);
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
            default:
                throw new ElepyConfigException(String.format("%s fields are not supported", fieldType.name()));

        }
    }

    private static void setupDefaultActions(Model<?> model) {

        var createPermissions = Optional
                .ofNullable(model.getJavaClass().getAnnotation(Create.class))
                .map(Create::requiredPermissions)
                .orElse(Permissions.DEFAULT);

        var updatePermissions = Optional
                .ofNullable(model.getJavaClass().getAnnotation(Update.class))
                .map(Update::requiredPermissions)
                .orElse(Permissions.DEFAULT);

        var deletePermissions = Optional
                .ofNullable(model.getJavaClass().getAnnotation(Delete.class))
                .map(Delete::requiredPermissions)
                .orElse(Permissions.DEFAULT);

        var findPermissions = Optional
                .ofNullable(model.getJavaClass().getAnnotation(Find.class))
                .map(Find::requiredPermissions)
                .orElse(Permissions.NONE);


        model.setFindOneAction(HttpAction.of("Find One", model.getSlug() + "/:id", findPermissions, HttpMethod.GET, ActionType.SINGLE));
        model.setFindManyAction(HttpAction.of("Find Many", model.getSlug(), findPermissions, HttpMethod.GET, ActionType.MULTIPLE));
        model.setUpdateAction(HttpAction.of("Update", model.getSlug() + "/:id", updatePermissions, HttpMethod.PUT, ActionType.SINGLE));
        model.setDeleteAction(HttpAction.of("Delete", model.getSlug() + "/:id", deletePermissions, HttpMethod.DELETE, ActionType.SINGLE));
        model.setCreateAction(HttpAction.of("Create", model.getSlug(), createPermissions, HttpMethod.POST, ActionType.MULTIPLE));

    }

}
