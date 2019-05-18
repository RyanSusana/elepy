package com.elepy.utils;

import com.elepy.annotations.*;
import com.elepy.describers.Property;
import com.elepy.describers.props.*;
import com.elepy.exceptions.ElepyConfigException;
import com.elepy.models.FieldType;

import javax.persistence.Column;
import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Field;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ModelUtils {

    public static Set<Property> describeClass(Class cls) {
        return Stream.concat(
                Stream.of(cls.getDeclaredFields())
                        .filter(field -> !field.isAnnotationPresent(Hidden.class))
                        .map(ModelUtils::describeFieldOrMethod),
                Stream.of(cls.getDeclaredMethods())
                        .filter(method -> method.isAnnotationPresent(Generated.class))
                        .map(ModelUtils::describeFieldOrMethod)
        ).collect(Collectors.toSet());
    }

    public static Property describeFieldOrMethod(AccessibleObject accessibleObject) {
        Property property = new Property();

        final boolean idField;

        if (accessibleObject instanceof Field) {
            idField = ReflectionUtils.getIdField(((Field) accessibleObject).getDeclaringClass()).map(field1 -> field1.getName().equals(((Field) accessibleObject).getName())).orElse(false);
        } else {
            idField = false;
        }


        final Column column = accessibleObject.getAnnotation(Column.class);
        final Importance importance = accessibleObject.getAnnotation(Importance.class);

        property.setName(ReflectionUtils.getPropertyName(accessibleObject));
        property.setPrettyName(ReflectionUtils.getPrettyName(accessibleObject));
        property.setRequired(accessibleObject.getAnnotation(Required.class) != null);
        property.setEditable(!idField && (!accessibleObject.isAnnotationPresent(Uneditable.class) || (column != null && !column.updatable())));
        property.setImportance(importance == null ? 0 : importance.value());
        property.setUnique(accessibleObject.isAnnotationPresent(Unique.class) || (column != null && column.unique()));
        property.setGenerated(accessibleObject.isAnnotationPresent(Generated.class) || (idField && !accessibleObject.isAnnotationPresent(Identifier.class)) || (idField && accessibleObject.isAnnotationPresent(Identifier.class) && accessibleObject.getAnnotation(Identifier.class).generated()));

        property.config(mapFieldTypeInformation(accessibleObject));
        return property;
    }

    private static PropertyConfig mapFieldTypeInformation(AccessibleObject field) {
        FieldType fieldType = FieldType.guessType(field);

        switch (fieldType) {
            case TEXT:
                return TextPropertyConfig.of(field);
            case DATE:
                return DatePropertyConfig.of(field);
            case NUMBER:
                return NumberPropertyConfig.of(field);
            case ENUM:
                return EnumPropertyConfig.of(field);
            case OBJECT:
                return ObjectPropertyConfig.of(field);
            case BOOLEAN:
                return BooleanPropertyConfig.of(field);
            case PRIMITIVE_ARRAY:
                return BasicArrayPropertyConfig.of(field);
            default:
                throw new ElepyConfigException(String.format("%s is not supported", fieldType.name()));

        }

    }

}
