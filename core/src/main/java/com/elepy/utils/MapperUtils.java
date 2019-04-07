package com.elepy.utils;

import com.elepy.annotations.Uneditable;
import com.elepy.exceptions.ElepyException;
import com.elepy.models.FieldType;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class MapperUtils {
    private MapperUtils() {
    }

    public static <T> T objectFromMaps(ObjectMapper objectMapper, Map<String, Object> objectAsMap, Map<String, Object> fieldsToAdd, Class<T> cls) {


        final Field idField = ReflectionUtils.getIdField(cls).orElseThrow(() -> new ElepyException("No id field", 500));
        fieldsToAdd.forEach((fieldName, fieldObject) -> {
            final Optional<Field> fieldWithName = ReflectionUtils.findFieldWithName(cls, fieldName);

            if (fieldWithName.isPresent()) {
                Field field = fieldWithName.get();
                FieldType fieldType = FieldType.guessType(field);
                if (fieldType.isPrimitive() && !idField.getName().equals(field.getName()) && shouldEdit(field)) {
                    objectAsMap.put(fieldName, fieldObject);
                }
            } else {
                throw new ElepyException(String.format("Unknown field: %s", fieldName));
            }
        });
        return objectMapper.convertValue(objectAsMap, cls);
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
} 
