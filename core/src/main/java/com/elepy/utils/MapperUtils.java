package com.elepy.utils;

import com.elepy.annotations.DateTime;
import com.elepy.annotations.PrettyName;
import com.elepy.annotations.TrueFalse;
import com.elepy.annotations.Uneditable;
import com.elepy.exceptions.ElepyConfigException;
import com.elepy.exceptions.ElepyException;
import com.elepy.models.FieldType;
import com.elepy.models.NumberType;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.Serializable;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.*;

public class MapperUtils {
    private MapperUtils() {
    }

    public static <T> T objectFromMaps(ObjectMapper objectMapper, Map<String, Object> objectAsMap, Map<String, Object> fieldsToAdd, Class<T> cls) {


        final Field idProperty = ReflectionUtils.getIdField(cls).orElseThrow(() -> new ElepyException("No id field", 500));
        fieldsToAdd.forEach((fieldName, fieldObject) -> {
            final Field field = ReflectionUtils.findFieldWithName(cls, fieldName).orElseThrow(() -> new ElepyException(String.format("Unknown field: %s", fieldName)));
            FieldType fieldType = FieldType.guessFieldType(field);
            if (fieldType.isPrimitive() && !idProperty.getName().equals(field.getName()) && shouldEdit(field)) {
                objectAsMap.put(fieldName, fieldObject);
            }

        });
        return objectMapper.convertValue(objectAsMap, cls);
    }

    /**
     * This method goes through an Enum's fields and maps the PrettyName of the field to the value of the Enum
     *
     * @param enumClass The class of the enum
     */
    public static List<Map<String, Object>> getEnumMapValues(Class<? extends Enum<?>> enumClass) {

        List<Map<String, Object>> toReturn = new ArrayList<>();
        for (Enum<?> enumConstant : enumClass.getEnumConstants()) {
            Map<String, Object> toAdd = new HashMap<>();

            toAdd.put("enumValue", enumConstant.name());

            Field declaredField;
            try {
                declaredField = enumClass.getDeclaredField(enumConstant.name());
            } catch (NoSuchFieldException ignored) {
                throw new ElepyConfigException("Enum field not found");
            }

            PrettyName annotation = Annotations.get(declaredField, PrettyName.class);
            if (annotation != null) {

                toAdd.put("enumName", annotation.value());
            } else {
                toAdd.put("enumName", enumConstant.name());
            }
            toReturn.add(toAdd);

        }
        return toReturn;
    }

    private static boolean shouldEdit(Field field) {
        final List<Class<? extends Annotation>> dontEdit = Collections.singletonList(Uneditable.class);

        for (Annotation annotation : field.getAnnotations()) {
            if (dontEdit.contains(annotation.annotationType())) {
                return false;
            }
        }
        return true;
    }

    public static Serializable toValueFromString(Field field, FieldType fieldType, String value) {
        if (fieldType.equals(FieldType.ENUM)) {
            return toEnumFromString(field, value);
        }
        if (fieldType.equals(FieldType.BOOLEAN)) {
            return toBooleanFromString(field, value);
        }
        if (fieldType.equals(FieldType.NUMBER)) {
            return toNumberFromString(field, value);
        } else if (fieldType.equals(FieldType.DATE)) {
            final DateTime annotation = Annotations.get(field, DateTime.class);
            final String format;

            if (annotation == null) {
                format = "";
            } else {
                format = annotation.format();
            }
            return toDateFromString(value, format);
        } else {
            return value;
        }
    }

    public static Serializable toEnumFromString(Field field, String value) {
        final List<Map<String, Object>> enumMapList = getEnumMapValues((Class<? extends Enum<?>>) field.getType());
        final Optional<Map<String, Object>> enumValue = enumMapList.stream().filter(enumMap -> enumMap.get("enumValue").toString().equalsIgnoreCase(value)).findFirst();


        if (enumValue.isPresent()) {
            return enumValue.get().get("enumValue").toString();
        }
        return value;
    }

    public static Serializable toDateFromString(String value, String format) {
        Date date = DateUtils.guessDate(value, format);

        if (date == null) {
            return value;
        } else {
            return date;
        }
    }

    public static Serializable toNumberFromString(Field field, String value) {
        NumberType numberType = NumberType.guessType(field);
        if (numberType.equals(NumberType.INTEGER)) {
            return Long.parseLong(value);
        } else {
            return Float.parseFloat(value);
        }
    }

    public static Serializable toBooleanFromString(Field field, String value) {
        final TrueFalse annotation = com.elepy.utils.Annotations.get(field,TrueFalse.class);

        if (annotation != null && value.equalsIgnoreCase(annotation.trueValue())) {
            return true;
        }
        return Boolean.parseBoolean(value);
    }
} 
