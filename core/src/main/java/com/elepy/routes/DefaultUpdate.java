package com.elepy.routes;

import com.elepy.dao.Crud;
import com.elepy.describers.ModelContext;
import com.elepy.evaluators.DefaultIntegrityEvaluator;
import com.elepy.evaluators.ObjectEvaluator;
import com.elepy.exceptions.ElepyException;
import com.elepy.exceptions.Message;
import com.elepy.http.HttpContext;
import com.elepy.http.Request;
import com.elepy.http.Response;
import com.elepy.utils.MapperUtils;
import com.elepy.utils.ReflectionUtils;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.*;

public class DefaultUpdate<T> implements UpdateHandler<T> {

    private T beforeUpdate;

    private static Map<String, Object> splitQuery(String body) throws UnsupportedEncodingException {
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

    public T update(T update, Crud<T> dao, List<ObjectEvaluator<T>> objectEvaluators, Class<T> tClass) throws Exception {
        for (ObjectEvaluator<T> objectEvaluator : objectEvaluators) {
            if (update != null) {
                objectEvaluator.evaluate(update, tClass);
            }
        }

        new DefaultIntegrityEvaluator<T>().evaluate(update, dao);
        dao.update(update);

        return update;
    }

    @SuppressWarnings("unchecked")
    public T updatedObjectFromRequest(T before, Request request, ObjectMapper objectMapper, Class<T> clazz) throws IOException {

        final String body = request.body();
        if (request.method().equals("PUT")) {
            if (body.startsWith("{")) {
                return objectMapper.readValue(body, clazz);
            } else {
                return MapperUtils.objectFromMaps(objectMapper, new HashMap<>(), splitQuery(request.body()), clazz);
            }
        } else {
            if (body.startsWith("{")) {
                final Map<String, Object> beforeMap = objectMapper.convertValue(before, Map.class);
                final Map<String, Object> changesMap = objectMapper.readValue(request.body(), Map.class);
                ReflectionUtils.getId(before).ifPresent(id -> changesMap.put("id", id));
                return MapperUtils.objectFromMaps(objectMapper, beforeMap, changesMap, clazz);
            } else {
                return setParamsOnObject(request, objectMapper, before, clazz);
            }
        }
    }

    public T update(Request request, Response response, Crud<T> dao, List<ObjectEvaluator<T>> objectEvaluators, Class<T> clazz, ObjectMapper objectMapper) throws Exception {
        String body = request.body();

        if (body == null || body.isEmpty()) {
            throw new ElepyException("No changes detected.");
        }

        Optional<T> before = dao.getById(request.modelId());

        if (!before.isPresent()) {
            throw new ElepyException("No object found with this ID", 404);
        }

        final T updated = updatedObjectFromRequest(before.get(), request, objectMapper, clazz);

        this.beforeUpdate = before.get();
        update(updated, dao, objectEvaluators, clazz);

        response.result(Message.of("Successfully updated item", 200));
        return updated;
    }


    @SuppressWarnings("unchecked")
    public T setParamsOnObject(Request request, ObjectMapper objectMapper, T object, Class<T> modelClass) throws UnsupportedEncodingException {

        Map<String, Object> map = objectMapper.convertValue(object, Map.class);
        Map<String, Object> splitQuery = splitQuery(request.body());

        return MapperUtils.objectFromMaps(objectMapper, map, splitQuery, modelClass);
    }

    @Override
    public void handleUpdatePut(HttpContext httpContext, Crud<T> dao, ModelContext<T> modelContext, ObjectMapper objectMapper) throws Exception {
        this.update(httpContext.request(), httpContext.response(), dao, modelContext.getObjectEvaluators(), modelContext.getModelType(), objectMapper);
    }

    @Override
    public void handleUpdatePatch(HttpContext httpContext, Crud<T> dao, ModelContext<T> modelContext, ObjectMapper objectMapper) throws Exception {
        this.update(httpContext.request(), httpContext.response(), dao, modelContext.getObjectEvaluators(), modelContext.getModelType(), objectMapper);
    }
}
