package com.elepy.utils;

import com.elepy.annotations.Identifier;
import com.elepy.annotations.Inject;
import com.elepy.annotations.PrettyName;
import com.elepy.annotations.Unique;
import com.elepy.di.ElepyContext;
import com.elepy.exceptions.ElepyConfigException;
import com.elepy.exceptions.ElepyException;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.jongo.marshall.jackson.oid.MongoId;

import javax.persistence.Column;
import javax.persistence.Id;
import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
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

    @SafeVarargs
    public static Optional<Field> searchForFieldWithAnnotation(Class cls, Class<? extends Annotation>... annotations) {
        return searchForFieldsWithAnnotation(cls, annotations).stream().findFirst();
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

        try {
            Field field = getIdField(object.getClass()).orElseThrow(() -> new ElepyException("No ID field found"));
            field.setAccessible(true);
            return Optional.ofNullable((String) field.get(object));
        } catch (IllegalAccessException e) {
            throw new ElepyException("Illegally accessing id field");
        }

    }


    public static Optional<Field> getIdField(Class cls) {

        Optional<Field> annotated = searchForFieldWithAnnotation(cls, Identifier.class, MongoId.class, Id.class);
        if (annotated.isPresent()) {
            return annotated;
        } else {
            return findFieldWithName(cls, "id");
        }
    }

    public static <T> Constructor<? extends T> emptyConstructor(Class<T> cls) {
        final Optional<Constructor<? extends T>> emptyConstructor = getEmptyConstructor(cls);

        if (emptyConstructor.isPresent()) {

            return emptyConstructor.get();
        }
        throw new ElepyConfigException("Elepy Object Constructor must be empty, with no parameters.");

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

    public static <T> Optional<Constructor<? extends T>> getEmptyConstructor(Class<?> cls) {
        return getConstructor(cls, 0);
    }

    public static <T> Optional<Constructor<? extends T>> getElepyConstructor(Class<?> cls) {
        for (Constructor constructor : cls.getConstructors()) {
            if (constructor.getParameterCount() == 1 && (constructor.getParameterTypes()[0].equals(ElepyContext.class))) {
                return Optional.of((Constructor<T>) constructor);
            }
        }

        return Optional.empty();
    }


    public static <T> T initializeElepyObject(Class<? extends T> cls, ElepyContext elepy) throws IllegalAccessException, InvocationTargetException, InstantiationException {
        T object = initializeElepyObjectConstructor(cls, elepy);
        injectElepyContextFields(elepy, object);
        injectFields(elepy, object);
        return object;
    }

    private static void injectElepyContextFields(ElepyContext elepyContext, Object object) throws IllegalAccessException {
        for (Field field : object.getClass().getDeclaredFields()) {
            field.setAccessible(true);
            if (field.getType().equals(ElepyContext.class) && field.get(object) == null) {
                field.set(object, elepyContext);
            }
        }
    }

    private static void injectFields(ElepyContext elepyContext, Object object) throws IllegalAccessException {
        List<Field> fields = searchForFieldsWithAnnotation(object.getClass(), Inject.class);

        for (Field field : fields) {
            Inject annotation = field.getAnnotation(Inject.class);
            Object contextObject = elepyContext.getSingleton(field.getType(), annotation.tag());
            field.set(object, contextObject);
        }
    }

    private static <T> T initializeElepyObjectConstructor(Class<? extends T> cls, ElepyContext elepy) throws IllegalAccessException, InvocationTargetException, InstantiationException {

        Optional<Constructor<? extends T>> emptyConstructor =
                ClassUtils.getEmptyConstructor(cls);

        if (emptyConstructor.isPresent()) {
            return emptyConstructor.get().newInstance();
        } else {
            Optional<Constructor<? extends T>> elepyConstructor =
                    ClassUtils.getElepyConstructor(cls);

            if (!elepyConstructor.isPresent()) {
                throw new ElepyConfigException(String.format("Can't initialize %s. It has no empty constructor or a constructor with just one ElepyContext.", cls.getName()));
            }
            return elepyConstructor.get().newInstance(elepy);
        }
    }

    public static <T> Optional<Constructor<? extends T>> getConstructor(Class<?> cls, int amountOfParams) {
        for (Constructor constructor : cls.getConstructors()) {
            if (constructor.getParameterCount() == amountOfParams) {
                return Optional.of((Constructor<T>) constructor);
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
