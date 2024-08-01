package com.elepy.schemas.options;

import com.elepy.annotations.*;
import com.elepy.schemas.FieldType;
import com.elepy.schemas.Property;
import com.elepy.schemas.PropertyFactory;
import com.elepy.utils.Annotations;
import com.elepy.utils.ReflectionUtils;
import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.Column;
import java.lang.reflect.AccessibleObject;
import java.lang.reflect.AnnotatedElement;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ObjectOptions implements Options {

    @Localized
    private String objectName;
    private String featuredProperty;
    private List<Property> properties;

    @JsonIgnore
    private final PropertyFactory propertyFactory;
    private ObjectOptions(Class<?> objectType,InnerObject annotation) {
        this();
        featuredProperty = getFeaturedProperty(objectType);

        objectName = getObjectName(objectType, annotation);

        this.properties = describeProperties(objectType, getRecursionDepth(annotation));
    }


    public ObjectOptions() {
        this.propertyFactory = new PropertyFactory();
    }

    public static ObjectOptions of(AnnotatedElement field) {
        Class<?> objectType = ReflectionUtils.returnTypeOf(field);
        final InnerObject annotation = Annotations.get(field, InnerObject.class);
        return new ObjectOptions(objectType, annotation);
    }
    // This method takes over the infinite recursion that would otherwise happen during ModelUtils.describeFieldOrMethod
    // It is meant to limit the amount of times the recursion  can happen, with a user-definable maxRecursionDepth property
    // If the max depth is met, this property is  set to null
    private Property createRecursiveObjectOptionsTree(AccessibleObject field, int maxDepth, int currentDepth) {
        if (maxDepth == currentDepth) {
            return null;
        } else {
            final Class<?> fieldType = ReflectionUtils.returnTypeOf(field);
            final boolean isArray = FieldType.guessFieldType(fieldType).equals(FieldType.ARRAY);
            final Class<?> objectType = isArray ? ReflectionUtils.getGenericType(field, 0) : fieldType;
            final Property property = new Property();

            setupPropertyBasics(field, false, property);

            final var options = new ObjectOptions();

            setOptions(field, property, options);

            options.setProperties(getAccessibleObjects(objectType).stream().map(accessibleObject -> {
                        if (isRecursive(objectType, accessibleObject)) {
                            return createRecursiveObjectOptionsTree(field, maxDepth, currentDepth + 1);
                        } else {
                            return propertyFactory.describeAccessibleObject(accessibleObject);
                        }

                    }).filter(Objects::nonNull)
                    .sorted()
                    .collect(Collectors.toList()));
            return property;
        }
    }


    public void setupPropertyBasics(AccessibleObject accessibleObject, boolean idProperty, Property property) {
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
    private static void setOptions(AccessibleObject field, Property property, ObjectOptions options) {

        final var fieldType = FieldType.guessFieldType(field);

        property.setType(fieldType);
        property.setOptions(fieldType.equals(FieldType.ARRAY) ? ArrayOptions.of(field, options) : options);

        final Class<?> objectType = ReflectionUtils.returnTypeOf(field);

        options.featuredProperty = getFeaturedProperty(objectType);

        options.objectName = getObjectName(objectType, null);

    }


    // This method should check if a collection or object is recursive in nature
    private static boolean isRecursive(Class<?> recursionTypeToCheck, AccessibleObject field) {
        final var isACollection = FieldType.guessFieldType(field).equals(FieldType.ARRAY);

        if (isACollection && ReflectionUtils.getGenericType(field, 0).equals(recursionTypeToCheck)) {
            return true;
        } else {
            final Class<?> fieldType = ReflectionUtils.returnTypeOf(field);

            return fieldType.equals(recursionTypeToCheck);
        }
    }


    private List<Property> describeProperties(Class cls, int recursionDepth) {
        return getAccessibleObjects(cls).stream()
                .map(accessibleObject -> {
                    if (isRecursive(cls, accessibleObject)) {
                        return createRecursiveObjectOptionsTree(accessibleObject, recursionDepth, 1);
                    } else {
                        return propertyFactory.describeAccessibleObject(accessibleObject);
                    }
                })
                .filter(Objects::nonNull)
                .sorted()
                .collect(Collectors.toList());
    }


    /**
     * TODO: refactor away
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

    private static String getObjectName(Class<?> objectType, InnerObject annotation) {
        return (annotation == null || annotation.name().isBlank()) ? objectType.getSimpleName() : annotation.name();
    }

    private static int getRecursionDepth(InnerObject annotation) {
        return annotation == null ? 3 : annotation.maxRecursionDepth();
    }

    private static String getFeaturedProperty(Class<?> objectType) {
        return ReflectionUtils.searchForFieldWithAnnotation(objectType, Featured.class)
                .map(ReflectionUtils::getPropertyName).orElse(null);
    }

    public void setProperties(List<Property> properties) {
        this.properties = properties;
    }

    public String getObjectName() {
        return objectName;
    }

    public String getFeaturedProperty() {
        return featuredProperty;
    }

    public List<Property> getProperties() {
        return properties;
    }
}
