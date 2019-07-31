package com.elepy.handlers;

import com.elepy.dao.Crud;
import com.elepy.evaluators.DefaultIntegrityEvaluator;
import com.elepy.evaluators.EvaluationType;
import com.elepy.evaluators.ObjectEvaluator;
import com.elepy.exceptions.ElepyException;
import com.elepy.exceptions.Message;
import com.elepy.http.HttpContext;
import com.elepy.http.Request;
import com.elepy.http.Response;
import com.elepy.models.Model;
import com.elepy.models.ModelContext;
import com.elepy.utils.MapperUtils;
import com.elepy.utils.ReflectionUtils;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class DefaultUpdate<T> implements UpdateHandler<T> {

    private T beforeUpdate;

    private static Map<String, Object> splitQuery(String body) throws UnsupportedEncodingException {
        Map<String, Object> queryPairs = new LinkedHashMap<>();
        String[] pairs = body.split("&");
        for (String pair : pairs) {
            int idx = pair.indexOf('=');
            queryPairs.put(URLDecoder.decode(pair.substring(0, idx), StandardCharsets.UTF_8), URLDecoder.decode(pair.substring(idx + 1), StandardCharsets.UTF_8));
        }
        return queryPairs;
    }

    public T before() {
        if (beforeUpdate == null) {
            throw new IllegalStateException("Before not yet set!");
        }
        return beforeUpdate;
    }

    public T update(T update, ModelContext<T> modelContext, List<ObjectEvaluator<T>> objectEvaluators, Model<T> tClass) throws Exception {
        for (ObjectEvaluator<T> objectEvaluator : objectEvaluators) {
            if (update != null) {
                objectEvaluator.evaluate(update);
            }
        }

        new DefaultIntegrityEvaluator<T>(modelContext).evaluate(update, EvaluationType.UPDATE);
        modelContext.getCrud().update(update);

        return update;
    }

    @SuppressWarnings("unchecked")
    public T updatedObjectFromRequest(T before, Request request, ObjectMapper objectMapper, Model<T> model) throws IOException {

        final String body = request.body();
        if (request.method().equals("PUT")) {
            if (body.startsWith("{")) {
                return objectMapper.readValue(body, model.getJavaClass());
            } else {
                return MapperUtils.objectFromMaps(objectMapper, new HashMap<>(), splitQuery(request.body()), model.getJavaClass());
            }
        } else {
            if (body.startsWith("{")) {
                final Map<String, Object> beforeMap = objectMapper.convertValue(before, Map.class);
                final Map<String, Object> changesMap = objectMapper.readValue(request.body(), Map.class);
                ReflectionUtils.getId(before).ifPresent(id -> changesMap.put("id", id));
                return MapperUtils.objectFromMaps(objectMapper, beforeMap, changesMap, model.getJavaClass());
            } else {
                return setParamsOnObject(request, objectMapper, before, model.getJavaClass());
            }
        }
    }

    public T update(Request request, Response response, ModelContext<T> modelContext, List<ObjectEvaluator<T>> objectEvaluators, Model<T> model, ObjectMapper objectMapper) throws Exception {
        String body = request.body();

        if (body == null || body.isEmpty()) {
            throw new ElepyException("No changes detected.");
        }

        Optional<T> before = modelContext.getCrud().getById(request.modelId());

        if (!before.isPresent()) {
            throw new ElepyException("No object found with this ID", 404);
        }

        final T updated = updatedObjectFromRequest(before.get(), request, objectMapper, model);

        this.beforeUpdate = before.get();
        update(updated, modelContext, objectEvaluators, model);

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
        this.update(httpContext.request(), httpContext.response(), modelContext, modelContext.getObjectEvaluators(), modelContext.getModel(), objectMapper);
    }

    @Override
    public void handleUpdatePatch(HttpContext httpContext, Crud<T> dao, ModelContext<T> modelContext, ObjectMapper objectMapper) throws Exception {
        this.update(httpContext.request(), httpContext.response(), modelContext, modelContext.getObjectEvaluators(), modelContext.getModel(), objectMapper);
    }
}
