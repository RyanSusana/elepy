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

        return new ObjectOptions(objectName, featuredProperty, removeRecursiveProps(objectType, getRecursionDepth(annotation)));
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

    private static List<Property> removeRecursiveProps(Class cls, int recursionDepth) {
        return ModelUtils.getDeclaredFields(cls).stream()
                .map(accessibleObject -> {
                    if (isObjectRecursive(cls, accessibleObject)) {
                        return createRecursiveObjectOptionsTree(accessibleObject, recursionDepth, 1);
                    } else {
                        return ModelUtils.describeFieldOrMethod(accessibleObject);
                    }
                })
                .filter(Objects::nonNull)
                .sorted()
                .collect(Collectors.toList());
    }


    private static Property createRecursiveObjectOptionsTree(AccessibleObject field, int maxDepth, int currentDepth) {
        if (maxDepth == currentDepth) {
            return null;
        } else {
            final Property property = new Property();

            ModelUtils.setupPropertyBasics(field, false, property);

            property.setType(FieldType.OBJECT);
            property.setOptions(new ObjectOptions());


            final Class<?> objectType = setBasicExtras(field, property);

            final List<Property> properties = ModelUtils.getDeclaredFields(objectType).stream().map(accessibleObject -> {
                if (ReflectionUtils.returnTypeOf(accessibleObject).equals(objectType)) {
                    return createRecursiveObjectOptionsTree(field, maxDepth, currentDepth + 1);
                } else {
                    return ModelUtils.describeFieldOrMethod(accessibleObject);
                }

            }).filter(Objects::nonNull)
                    .sorted()
                    .collect(Collectors.toList());


            final ObjectOptions options = property.getOptions();
            options.properties = properties;

            return property;
        }
    }

    private static Class<?> setBasicExtras(AccessibleObject field, Property property) {
        final Class<?> objectType = ReflectionUtils.returnTypeOf(field);

        final ObjectOptions options = property.getOptions();
        options.featuredProperty = getFeaturedProperty(objectType);

        options.objectName = getObjectName(objectType, null);
        return objectType;
    }

    public static boolean isObjectRecursive(Class<?> recursionTypeToCheck, AccessibleObject prop) {
        final Class<?> baseFieldType = ReflectionUtils.returnTypeOf(prop);

        return baseFieldType.equals(recursionTypeToCheck);
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
