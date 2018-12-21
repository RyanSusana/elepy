package com.elepy.utils;

import com.elepy.annotations.Identifier;
import com.elepy.annotations.PrettyName;
import com.elepy.annotations.Unique;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.jongo.marshall.jackson.oid.MongoId;

import javax.persistence.Column;
import javax.persistence.Id;
import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class ClassUtils {

    private ClassUtils() {
    }

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
        } else if (hasId(field)) {
            return "id";
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

    private static boolean hasId(Field field) {
        return field.isAnnotationPresent(MongoId.class) || field.isAnnotationPresent(Identifier.class) || field.isAnnotationPresent(Id.class);
    }

    public static Optional<String> getId(Object object) {

        for (Field field : object.getClass().getDeclaredFields()) {
            field.setAccessible(true);
            if (hasId(field) || (field.getName().equals("id") && field.getType().equals(String.class))) {
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

            if (field.isAnnotationPresent(MongoId.class) || field.isAnnotationPresent(Identifier.class) || field.isAnnotationPresent(Id.class)) {
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

    public static <T> Constructor<T> emptyConstructor(Class<T> cls) {
        final Optional<Constructor> emptyConstructor = getEmptyConstructor(cls);

        if (emptyConstructor.isPresent()) {

            return (Constructor<T>) emptyConstructor.get();
        }
        throw new IllegalArgumentException("Elepy Object Constructor must be empty, with no parameters.");

    }

    public static Optional<Field> findFieldWithName(Class cls, String name) {
        for (Field field : cls.getDeclaredFields()) {
            if (field.isAnnotationPresent(JsonProperty.class)) {
                final JsonProperty annotation = field.getAnnotation(JsonProperty.class);

                if (annotation.value().equals(name)) {
                    return Optional.of(field);
                }
            } else {
                if (field.getName().equals(name)) {
                    return Optional.of(field);
                }
            }
        }
        return Optional.empty();
    }

    public static Optional<Constructor> getEmptyConstructor(Class<?> cls) {
        for (Constructor constructor : cls.getConstructors()) {
            if (constructor.getParameterCount() == 0) {
                return Optional.of(constructor);
            }
        }

        return Optional.empty();
    }

    public static boolean hasIntegrityRules(Class<?> cls) {
        final List<Field> fields = searchForFieldsWithAnnotation(cls, Unique.class);


        return !fields.isEmpty() || hasJPAIntegrityRules(cls);
    }

    private static boolean hasJPAIntegrityRules(Class<?> cls) {
        final List<Field> fields = searchForFieldsWithAnnotation(cls, Column.class);

        for (Field field : fields) {

            final Column unique = field.getAnnotation(Column.class);

            if (unique.unique()) {
                return true;
            }
        }
        return false;
    }

    public static List<Field> getUniqueFields(Class cls) {
        List<Field> uniqueFields = searchForFieldsWithAnnotation(cls, Unique.class);
        uniqueFields.addAll(searchForFieldsWithAnnotation(cls, Column.class).stream().filter(field -> {
            final Column column = field.getAnnotation(Column.class);
            return column.unique();
        }).collect(Collectors.toList()));

        return uniqueFields;
    }

}
