package com.elepy.utils;

import com.elepy.annotations.*;
import com.elepy.exceptions.ElepyConfigException;
import com.elepy.exceptions.ElepyException;
import com.elepy.http.Route;
import com.elepy.http.*;
import com.fasterxml.jackson.annotation.JsonProperty;

import javax.persistence.Column;
import javax.persistence.Id;
import java.io.Serializable;
import java.lang.annotation.Annotation;
import java.lang.reflect.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.elepy.http.RouteBuilder.anElepyRoute;

public class ReflectionUtils {

    private ReflectionUtils() {
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

    public static String getPropertyName(AccessibleObject property) {
        JsonProperty jsonProperty = property.getAnnotation(JsonProperty.class);
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
        for (Field declaredField : cls.getDeclaredFields()) {
            declaredField.setAccessible(true);
            if (getPropertyName(declaredField).equals(property)) {
                return declaredField;
            }
        }
        return null;
    }

    public static String getPrettyName(AccessibleObject field) {
        if (field.isAnnotationPresent(PrettyName.class)) {
            return field.getAnnotation(PrettyName.class).value();
        }
        return getPropertyName(field);
    }

    private static boolean hasId(Field field) {
        return field.isAnnotationPresent(Identifier.class) || field.isAnnotationPresent(Id.class);
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

    public static Serializable getIdOrThrow(Object object) {

        try {
            Field field = getIdField(object.getClass()).orElseThrow(() -> new ElepyException("No ID field found"));
            field.setAccessible(true);
            return Optional.ofNullable((Serializable) field.get(object)).orElseThrow(() -> new ElepyException("No ID found", 404));
        } catch (IllegalAccessException e) {
            throw new ElepyException("Illegally accessing id field");
        }

    }

    public static Object getPropertyFromObject(String property, Object obj) {
        try {
            return Optional.ofNullable(ReflectionUtils.getPropertyField(obj.getClass(), property))
                    .orElseThrow(() -> new ElepyException(String.format("Property '%s' not found on the class '%s'", property, obj.getClass().getName()), 500))
                    .get(obj);
        } catch (IllegalAccessException e) {
            throw new ElepyException("Failed to reflectively access an object", 500, e);
        }

    }

    public static void setPropertyOnObject(String propertyName, Object object, Object value) {
        try {
            ReflectionUtils.getPropertyField(object.getClass(), propertyName).set(object, value);
        } catch (IllegalAccessException e) {
            throw new ElepyException("Failed to reflectively access an object", 500, e);
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

    public static Class<?> returnTypeOf(AccessibleObject field) {
        return (field instanceof Field) ? ((Field) field).getType() : ((Method) field).getReturnType();
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

        getIdField(cls).ifPresent(uniqueFields::add);

        return uniqueFields;
    }


    public static Route routeFromMethod(Object obj, Method method) {
        com.elepy.annotations.Route annotation = method.getAnnotation(com.elepy.annotations.Route.class);
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


    /**
     * Tries to get a tag from a {@link Field} or {@link Parameter}, defaults to null
     *
     * @param type Field or Parameter
     * @return the guessed field
     */
    public static String getDependencyTag(AnnotatedElement type) {
        Inject injectAnnotation = type.getAnnotation(Inject.class);

        if (injectAnnotation != null && !injectAnnotation.tag().isEmpty()) {
            return injectAnnotation.tag();
        }

        Tag tag = type.getAnnotation(Tag.class);
        if (tag != null && !tag.value().isEmpty()) {
            return tag.value();
        }

        if (type instanceof Field) {
            Class<?> fieldType = ((Field) type).getType();

            tag = fieldType.getAnnotation(Tag.class);
            if (tag != null) {
                return tag.value();
            }
            try {
                final Class<?> genericType = (Class) ((ParameterizedType) ((Field) type).getGenericType()).getActualTypeArguments()[0];
                if (genericType != null) {
                    RestModel restModel = genericType.getAnnotation(RestModel.class);
                    if (restModel != null) {
                        return restModel.slug();
                    }
                }
            } catch (ClassCastException ignored) {
                return null;
            }
        }
        return null;
    }

    public static <T> Optional<Constructor<? extends T>> getElepyAnnotatedConstructor(Class<?> cls) {
        for (Constructor constructor : cls.getConstructors()) {
            if (constructor.isAnnotationPresent(ElepyConstructor.class)) {
                return Optional.of((Constructor<T>) constructor);
            }
        }
        return Optional.empty();
    }
}
