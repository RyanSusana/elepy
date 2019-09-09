package com.elepy.models.options;

import com.elepy.annotations.Featured;
import com.elepy.annotations.InnerObject;
import com.elepy.models.FieldType;
import com.elepy.models.Property;
import com.elepy.utils.ModelUtils;
import com.elepy.utils.ReflectionUtils;

import java.lang.reflect.AccessibleObject;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class ObjectOptions implements Options {

    private String objectName;
    private String featuredProperty;
    private List<Property> properties;

    private ObjectOptions(String objectName, String featuredProperty, List<Property> properties) {
        this.objectName = objectName;
        this.featuredProperty = featuredProperty;
        this.properties = properties;
    }

    private ObjectOptions() {

    }

    public static ObjectOptions of(AccessibleObject field) {
        Class<?> objectType = ReflectionUtils.returnTypeOf(field);
        final InnerObject annotation = field.getAnnotation(InnerObject.class);
        return of(objectType, annotation);
    }

    public static ObjectOptions of(Class<?> objectType, InnerObject annotation) {
        final String featuredProperty = getFeaturedProperty(objectType);

        final String objectName = getObjectName(objectType, annotation);

        return new ObjectOptions(objectName, featuredProperty, describeProperties(objectType, getRecursionDepth(annotation)));
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

    private static List<Property> describeProperties(Class cls, int recursionDepth) {
        return ModelUtils.getDeclaredProperties(cls).stream()
                .map(accessibleObject -> {
                    if (isRecursive(cls, accessibleObject)) {
                        return createRecursiveObjectOptionsTree(accessibleObject, recursionDepth, 1);
                    } else {
                        return ModelUtils.describeFieldOrMethod(accessibleObject);
                    }
                })
                .filter(Objects::nonNull)
                .sorted()
                .collect(Collectors.toList());
    }


    // This method takes over the infinite recursion that would otherwise happen during ModelUtils.describeFieldOrMethod
    // It is meant to limit the amount of times the recursion  can happen, with a user-definable maxRecursionDepth property
    // If the max depth is met, this property is  set to null
    private static Property createRecursiveObjectOptionsTree(AccessibleObject field, int maxDepth, int currentDepth) {
        if (maxDepth == currentDepth) {
            return null;
        } else {
            final Class<?> fieldType = ReflectionUtils.returnTypeOf(field);
            final boolean isArray = FieldType.guessByClass(fieldType).equals(FieldType.ARRAY);
            final Class<?> objectType = isArray ? ReflectionUtils.getGenericType(field, 0) : fieldType;
            final Property property = new Property();

            ModelUtils.setupPropertyBasics(field, false, property);


            final var options = new ObjectOptions();

            setOptions(field, property, options);


            options.properties = ModelUtils.getDeclaredProperties(objectType).stream().map(accessibleObject -> {
                if (isRecursive(objectType, accessibleObject)) {
                    return createRecursiveObjectOptionsTree(field, maxDepth, currentDepth + 1);
                } else {
                    return ModelUtils.describeFieldOrMethod(accessibleObject);
                }

            }).filter(Objects::nonNull)
                    .sorted()
                    .collect(Collectors.toList());

            return property;
        }
    }

    private static void setOptions(AccessibleObject field, Property property, ObjectOptions options) {

        final var fieldType = FieldType.guessType(field);

        property.setType(fieldType);
        property.setOptions(fieldType.equals(FieldType.ARRAY) ? ArrayOptions.of(field, options) : options);

        final Class<?> objectType = ReflectionUtils.returnTypeOf(field);

        options.featuredProperty = getFeaturedProperty(objectType);

        options.objectName = getObjectName(objectType, null);

    }


    // This method should check if a collection or object is recursive in nature
    private static boolean isRecursive(Class<?> recursionTypeToCheck, AccessibleObject field) {
        final var isACollection = FieldType.guessType(field).equals(FieldType.ARRAY);

        if (isACollection && ReflectionUtils.getGenericType(field, 0).equals(recursionTypeToCheck)) {
            return true;
        } else {
            final Class<?> fieldType = ReflectionUtils.returnTypeOf(field);

            return fieldType.equals(recursionTypeToCheck);
        }
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
