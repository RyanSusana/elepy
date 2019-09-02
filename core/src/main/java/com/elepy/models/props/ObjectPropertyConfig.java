package com.elepy.models.props;

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

public class ObjectPropertyConfig implements PropertyConfig {
    private final String objectName;
    private final String featuredProperty;
    private final List<Property> properties;

    public ObjectPropertyConfig(String objectName, String featuredProperty, List<Property> properties) {
        this.objectName = objectName;
        this.featuredProperty = featuredProperty;
        this.properties = properties;
    }

    public static ObjectPropertyConfig of(AccessibleObject field) {
        Class<?> objectType = ReflectionUtils.returnTypeOf(field);
        final InnerObject annotation = field.getAnnotation(InnerObject.class);
        return of(objectType, annotation);
    }

    public static ObjectPropertyConfig of(Class<?> objectType, InnerObject annotation) {
        final String featuredProperty = getFeaturedProperty(objectType);

        final String objectName = getObjectName(objectType, annotation);

        return new ObjectPropertyConfig(objectName, featuredProperty, removeRecursiveProps(objectType, getRecursionDepth(annotation)));
    }

    private static String getObjectName(Class<?> objectType, InnerObject annotation) {
        return (annotation == null || annotation.name().isBlank()) ? objectType.getSimpleName() : annotation.name();
    }

    private static int getRecursionDepth(InnerObject annotation) {
        return annotation == null ? 3 : annotation.recursionDepth();
    }

    private static String getFeaturedProperty(Class<?> objectType) {
        return ReflectionUtils.searchForFieldWithAnnotation(objectType, Featured.class)
                .map(ReflectionUtils::getPropertyName).orElse(null);
    }

    private static List<Property> removeRecursiveProps(Class cls, int recursionDepth) {
        return ModelUtils.getDeclaredFields(cls).stream()
                .map(accessibleObject -> {
                    if (isObjectRecursive(cls, accessibleObject)) {
                        return ObjectPropertyConfig.createRecursiveObjectPropertyTree(accessibleObject, recursionDepth, 0);
                    } else {
                        return ModelUtils.describeFieldOrMethod(accessibleObject);
                    }
                })
                .filter(Objects::nonNull)
                .sorted()
                .collect(Collectors.toList());
    }


    private static Property createRecursiveObjectPropertyTree(AccessibleObject field, int maxDepth, int currentDepth) {
        if (maxDepth == currentDepth) {
            return null;
        } else {
            final Property property = ModelUtils.createBasicProperty(field, false);

            property.setType(FieldType.OBJECT);


            final Class<?> objectType = setBasicExtras(field, property);

            final List<Property> properties = ModelUtils.getDeclaredFields(objectType).stream().map(accessibleObject -> {
                if (ReflectionUtils.returnTypeOf(accessibleObject).equals(objectType)) {
                    return createRecursiveObjectPropertyTree(field, maxDepth, currentDepth + 1);
                } else {
                    return ModelUtils.describeFieldOrMethod(accessibleObject);
                }

            }).filter(Objects::nonNull)
                    .sorted()
                    .collect(Collectors.toList());

            property.setExtra("properties", properties);

            return property;
        }
    }

    private static Class<?> setBasicExtras(AccessibleObject field, Property property) {
        final Class<?> objectType = ReflectionUtils.returnTypeOf(field);
        final String featuredProperty = getFeaturedProperty(objectType);

        final String objectName = getObjectName(objectType, null);

        property.setExtra("objectName", objectName);
        property.setExtra("featuredProperty", featuredProperty);
        return objectType;
    }

    public static boolean isObjectRecursive(Class<?> recursionTypeToCheck, AccessibleObject prop) {
        final Class<?> baseFieldType = ReflectionUtils.returnTypeOf(prop);

        return baseFieldType.equals(recursionTypeToCheck);
    }

    public static ObjectPropertyConfig of(Property property) {
        return new ObjectPropertyConfig(property.getExtra("objectName"), property.getExtra("featuredProperty"), property.getExtra("properties"));
    }


    @Override
    public void config(Property property) {
        property.setType(FieldType.OBJECT);
        property.setExtra("objectName", objectName);
        property.setExtra("featuredProperty", featuredProperty);
        property.setExtra("properties", properties);
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
