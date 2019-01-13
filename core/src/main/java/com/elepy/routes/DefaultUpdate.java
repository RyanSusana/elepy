package com.elepy.routes;

import com.elepy.concepts.IntegrityEvaluatorImpl;
import com.elepy.concepts.ObjectEvaluator;
import com.elepy.concepts.ObjectUpdateEvaluatorImpl;
import com.elepy.dao.Crud;
import com.elepy.di.ElepyContext;
import com.elepy.exceptions.ElepyException;
import com.elepy.models.FieldType;
import com.elepy.utils.ClassUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
import spark.Request;
import spark.Response;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Field;
import java.net.URLDecoder;
import java.util.*;

public class DefaultUpdate<T> implements UpdateHandler<T> {

    private T beforeUpdate;

    public static Map<String, Object> splitQuery(String body) throws UnsupportedEncodingException {
        Map<String, Object> queryPairs = new LinkedHashMap<>();
        String[] pairs = body.split("&");
        for (String pair : pairs) {
            int idx = pair.indexOf('=');
            queryPairs.put(URLDecoder.decode(pair.substring(0, idx), "UTF-8"), URLDecoder.decode(pair.substring(idx + 1), "UTF-8"));
        }
        return queryPairs;
    }

    public T before() {
        if (beforeUpdate == null) {
            throw new IllegalStateException("Before not yet set!");
        }
        return beforeUpdate;
    }

    public T update(T before, T update, Crud<T> dao, List<ObjectEvaluator<T>> objectEvaluators, Class<T> tClass) throws Exception {

        ObjectUpdateEvaluatorImpl<T> updateEvaluator = new ObjectUpdateEvaluatorImpl<>();

        updateEvaluator.evaluate(before, update);

        for (ObjectEvaluator<T> objectEvaluator : objectEvaluators) {
            if (update != null) {
                objectEvaluator.evaluate(update, tClass);
            }
        }

        new IntegrityEvaluatorImpl<T>().evaluate(update, dao);
        dao.update(update);

        return update;
    }

    public T updatedObjectFromRequest(T before, Request request, ObjectMapper objectMapper, Class<T> clazz) throws IOException {

        final String body = request.body();
        if (request.requestMethod().equals("PUT")) {
            if (body.startsWith("{")) {
                return objectMapper.readValue(body, clazz);
            } else {
                return objectFromMaps(new HashMap<>(), splitQuery(request.body()), objectMapper, clazz);
            }
        } else {
            if (body.startsWith("{")) {
                final Map<String, Object> beforeMap = objectMapper.convertValue(before, Map.class);
                final Map<String, Object> changesMap = objectMapper.readValue(request.body(), Map.class);
                ClassUtils.getId(before).ifPresent(id -> changesMap.put("id", id));
                return objectFromMaps(beforeMap, changesMap, objectMapper, clazz);
            } else {
                return setParamsOnObject(request, objectMapper, before);
            }
        }
    }

    public T update(Request request, Response response, Crud<T> dao, ElepyContext elepy, List<ObjectEvaluator<T>> objectEvaluators, Class<T> clazz) throws Exception {
        String body = request.body();

        if (body == null || body.isEmpty()) {
            throw new ElepyException("No changes detected.");
        }

        Optional<T> before = dao.getById(request.params("id"));

        if (!before.isPresent()) {
            response.status(404);
            throw new ElepyException("No object found with this ID");
        }
        final T updated = updatedObjectFromRequest(before.get(), request, elepy.getObjectMapper(), clazz);

        this.beforeUpdate = before.get();
        update(beforeUpdate, updated, dao, objectEvaluators, clazz);

        response.status(200);
        response.body("OK");
        return updated;
    }

    public T objectFromMaps(Map<String, Object> objectAsMap, Map<String, Object> fieldsToAdd, ObjectMapper objectMapper, Class cls) {

        fieldsToAdd.forEach((fieldName, fieldObject) -> {
            final Optional<Field> fieldWithName = ClassUtils.findFieldWithName(cls, fieldName);

            if (fieldWithName.isPresent()) {
                Field field = fieldWithName.get();
                FieldType fieldType = FieldType.guessType(field);
                if (fieldType.isPrimitive()) {
                    objectAsMap.put(fieldName, fieldObject);
                }
            } else {
                throw new ElepyException(String.format("Unknown field: %s", fieldName));
            }
        });
        return (T) objectMapper.convertValue(objectAsMap, cls);
    }


    public T setParamsOnObject(Request request, ObjectMapper objectMapper, T object) throws UnsupportedEncodingException {

        Map<String, Object> map = objectMapper.convertValue(object, Map.class);
        Map<String, Object> splitQuery = splitQuery(request.body());

        return objectFromMaps(map, splitQuery, objectMapper, object.getClass());
    }

    @Override
    public void handleUpdate(Request request, Response response, Crud<T> dao, ElepyContext elepy, List<ObjectEvaluator<T>> objectEvaluators, Class<T> clazz) throws Exception {
        this.update(request, response, dao, elepy, objectEvaluators, clazz);
    }
}
