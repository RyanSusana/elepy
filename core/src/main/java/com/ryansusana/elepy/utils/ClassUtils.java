package com.ryansusana.elepy.utils;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.ryansusana.elepy.annotations.PrettyName;
import org.jongo.marshall.jackson.oid.MongoId;

import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ClassUtils {


    @SafeVarargs
    public static List<Field> searchForFieldsWithAnnotation(Class cls, Class<? extends Annotation>... annotations) {
        List<Field> fields = new ArrayList<>();
        for (Field field : cls.getDeclaredFields()) {
            field.setAccessible(true);
            for (Class<? extends Annotation> annotation : annotations) {
                if (field.isAnnotationPresent(annotation)) {
                    fields.add(field);
                    break;
                }
            }

        }
        return fields;
    }

    public static String getPropertyName(Field field) {
        if (field.isAnnotationPresent(JsonProperty.class)) {
            return field.getAnnotation(JsonProperty.class).value();
        } else if (field.isAnnotationPresent(MongoId.class)) {
            return "_id";
        } else {
            return field.getName();
        }
    }

    public static String getPrettyName(Field field) {
        if (field.isAnnotationPresent(PrettyName.class)) {
            return field.getAnnotation(PrettyName.class).value();
        }
        return getPropertyName(field);
    }

    public static Optional<String> getId(Object object) {

        for (Field field : object.getClass().getDeclaredFields()) {

            if (field.getAnnotation(MongoId.class) != null) {
                field.setAccessible(true);

                try {
                    return Optional.ofNullable((String) field.get(object));
                } catch (IllegalAccessException | ClassCastException e) {
                    throw new IllegalStateException(object.getClass().getName() + ": " + e.getMessage());
                }
            }
        }
        for (Field field : object.getClass().getDeclaredFields()) {

            if (field.getName().equals("id") && field.getType().equals(String.class)) {
                try {
                    return Optional.ofNullable((String) field.get(object));
                } catch (IllegalAccessException | ClassCastException e) {
                    throw new IllegalStateException(object.getClass().getName() + ": " + e.getMessage());
                }
            }
        }
        return Optional.empty();

    }

    public static Field getIdField(Class cls) {
        for (Field field : cls.getDeclaredFields()) {

            if (field.getAnnotation(MongoId.class) != null) {
                field.setAccessible(true);

                try {
                    return field;
                } catch (ClassCastException e) {
                    throw new IllegalStateException(cls.getName() + ": " + e.getMessage());
                }
            }
        }
        return null;
    }

    public static Optional<Constructor<?>> getEmptyConstructor(Class<?> cls) {
        for (Constructor<?> constructor : cls.getConstructors()) {
            if (constructor.getParameterCount() == 0) {
                return Optional.of(constructor);
            }
        }
        return Optional.empty();
    }

}
