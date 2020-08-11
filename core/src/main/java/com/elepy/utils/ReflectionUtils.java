package com.elepy.utils;

import com.elepy.annotations.ElepyConstructor;
import com.elepy.annotations.Identifier;
import com.elepy.annotations.Label;
import com.elepy.annotations.Unique;
import com.elepy.exceptions.ElepyConfigException;
import com.elepy.exceptions.ElepyException;
import com.elepy.http.*;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.googlecode.gentyref.GenericTypeReflector;

import javax.persistence.Column;
import javax.persistence.Id;
import java.io.Serializable;
import java.lang.annotation.Annotation;
import java.lang.reflect.*;
import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.elepy.http.RouteBuilder.anElepyRoute;

public class ReflectionUtils {

    private ReflectionUtils() {
    }


    private static Optional<Field> findFieldThatMatches(Class<?> root, Predicate<Field> predicate) {
        for (Field declaredField : root.getDeclaredFields()) {
            declaredField.setAccessible(true);
            if (predicate.test(declaredField)) {
                return Optional.of(declaredField);
            }
        }
        if (root.getSuperclass() != null) {
            return findFieldThatMatches(root.getSuperclass(), predicate);
        } else {
            return Optional.empty();
        }
    }

    public static List<Field> findFieldsThatMatch(Class<?> root, Predicate<Field> predicate) {
        return getAllFields(new LinkedList<>(), root)
                .stream()
                .peek(field -> field.setAccessible(true))
                .filter(predicate).collect(Collectors.toList());
    }

    public static List<Field> getAllFields(Class<?> type) {
        return getAllFields(new LinkedList<>(), type);
    }

    private static List<Field> getAllFields(List<Field> fields, Class<?> type) {
        fields.addAll(Arrays.asList(type.getDeclaredFields()));

        if (type.getSuperclass() != null && !type.getSuperclass().equals(Object.class)) {
            getAllFields(fields, type.getSuperclass());
        }

        return fields;
    }


    public static String getJavaName(AccessibleObject object) {
        if (object instanceof Field) {
            return ((Field) object).getName();
        } else {
            return ((Method) object).getName();
        }
    }

    @SafeVarargs
    public static List<Field> searchForFieldsWithAnnotation(Class cls, Class<? extends Annotation>... annotations) {
        return findFieldsThatMatch(cls,
                field -> Stream.of(annotations).anyMatch(field::isAnnotationPresent)
        );
    }

    @SafeVarargs
    public static Optional<Field> searchForFieldWithAnnotation(Class cls, Class<? extends Annotation>... annotations) {
        return findFieldThatMatches(cls, field -> Stream.of(annotations)
                .anyMatch(field::isAnnotationPresent));
    }

    public static String getPropertyName(AccessibleObject property) {
        JsonProperty jsonProperty = Annotations.get(property, JsonProperty.class);
        if (jsonProperty != null) {
            return jsonProperty.value();
        } else {
            if (property instanceof Field) {
                return ((Field) property).getName();
            } else if (property instanceof Method) {
                return ((Method) property).getName();
            }
            throw new ElepyConfigException("Failed to get the name from AccessibleObject");
        }
    }

    public static Field getPropertyField(Class<?> cls, String property) {
        return findFieldThatMatches(cls, field -> getPropertyName(field).equals(property)).orElse(null);
    }

    public static String getLabel(AccessibleObject field) {
        if (field.isAnnotationPresent(Label.class)) {
            return com.elepy.utils.Annotations.get(field, Label.class).value();
        }
        return getPropertyName(field);
    }

    public static Optional<Serializable> getId(Object object) {

        try {
            Field field = getIdField(object.getClass()).orElseThrow(() -> new ElepyException("No ID field found"));
            field.setAccessible(true);
            return Optional.ofNullable((Serializable) field.get(object));
        } catch (IllegalAccessException e) {
            throw new ElepyException("Illegally accessing id field");
        }

    }

    public static Serializable toObject(Class clazz, String value) {
        if (Boolean.class == clazz || boolean.class == clazz) return Boolean.valueOf(value);
        if (Byte.class == clazz || byte.class == clazz) return Byte.valueOf(value);
        if (Short.class == clazz || short.class == clazz) return Short.valueOf(value);
        if (Integer.class == clazz || int.class == clazz) return Integer.valueOf(value);
        if (Long.class == clazz || long.class == clazz) return Long.valueOf(value);
        if (Float.class == clazz || float.class == clazz) return Float.valueOf(value);
        if (Double.class == clazz || double.class == clazz) return Double.valueOf(value);
        return value;
    }

    public static Serializable toObjectIdFromString(Class tClass, String value) {
        Class<?> idType = ReflectionUtils.getIdField(tClass).orElseThrow(() -> new ElepyException("Can't find the ID field", 500)).getType();

        try {
            return toObject(idType, value);
        } catch (NumberFormatException e) {
            throw new ElepyException(String.format("'%s' is not a number", value));
        }
    }

    public static Optional<Field> getIdField(Class cls) {
        Optional<Field> annotated = searchForFieldWithAnnotation(cls, Identifier.class, Id.class);

        if (annotated.isPresent()) {
            return annotated;
        } else {
            return findFieldWithName(cls, "id");
        }
    }

    public static Class<?> returnTypeOf(AnnotatedElement field) {
        if (field instanceof AnnotatedType) {
            return (Class) ((AnnotatedType) field).getType();
        }
        if (field instanceof Parameter) {
            return GenericTypeReflector.erase(((Parameter) field).getType());
        }
        return (field instanceof Field) ? ((Field) field).getType() : ((Method) field).getReturnType();
    }

    public static Optional<Field> findFieldWithName(Class cls, String name) {
        return findFieldThatMatches(cls, field -> {
            if (field.isAnnotationPresent(JsonProperty.class)) {
                final JsonProperty annotation = com.elepy.utils.Annotations.get(field, JsonProperty.class);
                return annotation.value().equals(name);
            } else {
                return field.getName().equals(name);
            }
        });
    }

    public static <T> Optional<Constructor<? extends T>> getEmptyConstructor(Class<T> cls) {
        return getConstructor(cls, 0);
    }

    public static <T> Optional<Constructor<? extends T>> getConstructor(Class<?> cls, int amountOfParams) {
        for (Constructor constructor : cls.getDeclaredConstructors()) {
            constructor.setAccessible(true);
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
            final Column unique = com.elepy.utils.Annotations.get(field, Column.class);
            if (unique.unique()) {
                return true;
            }
        }
        return false;
    }

    public static List<Field> getUniqueFields(Class cls) {
        List<Field> uniqueFields = searchForFieldsWithAnnotation(cls, Unique.class);
        uniqueFields.addAll(searchForFieldsWithAnnotation(cls, Column.class).stream().filter(field -> {
            final Column column = com.elepy.utils.Annotations.get(field, Column.class);
            return column.unique();
        }).collect(Collectors.toList()));

        getIdField(cls).ifPresent(uniqueFields::add);

        return uniqueFields;
    }


    public static Route routeFromMethod(Object obj, Method method) {
        com.elepy.annotations.Route annotation = Annotations.get(method, com.elepy.annotations.Route.class);
        HttpContextHandler route;
        if (method.getParameterCount() == 0) {
            route = ctx -> {
                Object invoke = method.invoke(obj);
                if (invoke instanceof String) {
                    ctx.response().result((String) invoke);
                }
            };
        } else if (method.getParameterCount() == 2
                && method.getParameterTypes()[0].equals(Request.class)
                && method.getParameterTypes()[1].equals(Response.class)) {

            route = ctx -> {
                Object invoke = method.invoke(obj, ctx.request(), ctx.response());
                if (invoke instanceof String) {
                    ctx.response().result((String) invoke);
                }
            };

        } else if (method.getParameterCount() == 1 && method.getParameterTypes()[0].equals(HttpContext.class)) {
            route = ctx -> {
                Object invoke = method.invoke(obj, ctx);
                if (invoke instanceof String) {
                    ctx.response().result((String) invoke);
                }
            };
        } else {
            throw new ElepyConfigException("@HttpContextHandler annotated method must have no parameters or (Request, Response)");
        }
        return anElepyRoute()
                .addPermissions(annotation.requiredPermissions())
                .path(annotation.path())
                .method(annotation.method())
                .route(route)
                .build();
    }

    public static List<Route> scanForRoutes(Object obj) {
        List<Route> toReturn = new ArrayList<>();
        for (Method method : obj.getClass().getDeclaredMethods()) {
            method.setAccessible(true);
            if (method.isAnnotationPresent(com.elepy.annotations.Route.class)) {
                toReturn.add(routeFromMethod(obj, method));
            }
        }
        return toReturn;
    }


    public static Class getGenericType(AccessibleObject field, int parameterIndex) {
        return (Class) ((ParameterizedType) ((Field) field).getGenericType()).getActualTypeArguments()[parameterIndex];
    }

    public static <T> Optional<Constructor<? extends T>> getElepyConstructor(Class<T> cls) {
        for (Constructor constructor : cls.getConstructors()) {
            if (constructor.isAnnotationPresent(ElepyConstructor.class) || constructor.getParameterCount() == 0) {
                return Optional.of((Constructor<T>) constructor);
            }
        }
        return Optional.empty();
    }
}
