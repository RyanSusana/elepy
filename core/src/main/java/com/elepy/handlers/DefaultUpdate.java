package com.elepy.handlers;

import com.elepy.dao.Crud;
import com.elepy.evaluators.DefaultIntegrityEvaluator;
import com.elepy.evaluators.EvaluationType;
import com.elepy.evaluators.ObjectEvaluator;
import com.elepy.exceptions.ElepyException;
import com.elepy.exceptions.Message;
import com.elepy.http.HttpContext;
import com.elepy.http.Request;
import com.elepy.models.Schema;
import com.elepy.models.ModelContext;
import com.elepy.utils.MapperUtils;
import com.elepy.utils.ReflectionUtils;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class DefaultUpdate<T> implements UpdateHandler<T> {


    @SuppressWarnings("unchecked")
    protected T updatedObjectFromRequest(T before, Request request, ObjectMapper objectMapper, Schema<T> schema) throws IOException {

        final String body = request.body();
        if (request.method().equals("PUT")) {
            return objectMapper.readValue(body, schema.getJavaClass());
        } else {
            if (body.startsWith("{")) {
                final Map<String, Object> beforeMap = objectMapper.convertValue(before, Map.class);
                final Map<String, Object> changesMap = objectMapper.readValue(request.body(), Map.class);
                ReflectionUtils.getId(before).ifPresent(id -> changesMap.put("id", id));
                return MapperUtils.objectFromMaps(objectMapper, beforeMap, changesMap, schema.getJavaClass());
            } else {
                return setParamsOnObject(request, objectMapper, before, schema.getJavaClass());
            }
        }
    }

    public T handleUpdate(HttpContext context, ModelContext<T> modelContext, ObjectMapper objectMapper) throws Exception {
        String body = context.body();

        if (body == null || body.isEmpty()) {
            throw new ElepyException("No changes detected.");
        }

        T before = modelContext.getCrud().getById(context.recordId()).orElseThrow(() -> new ElepyException("No object found with this ID", 404));
        final T updated = updatedObjectFromRequest(before, context.request(), objectMapper, modelContext.getSchema());

        evaluateAndUpdate(context, updated, modelContext);

        context.result(Message.of("Successfully updated item", 200));
        return updated;
    }

    public T evaluateAndUpdate(HttpContext context, T update, ModelContext<T> modelContext) throws Exception {
        for (ObjectEvaluator<T> objectEvaluator : modelContext.getObjectEvaluators()) {
            if (update != null) {
                objectEvaluator.evaluate(update);
            }
        }

        context.validate(update);

        new DefaultIntegrityEvaluator<>(modelContext).evaluate(update, EvaluationType.UPDATE);
        modelContext.getCrud().update(update);

        return update;
    }

    @Override
    public void handleUpdatePut(HttpContext httpContext, Crud<T> dao, ModelContext<T> modelContext, ObjectMapper objectMapper) throws Exception {
        this.handleUpdate(httpContext, modelContext, objectMapper);
    }

    @Override
    public void handleUpdatePatch(HttpContext httpContext, Crud<T> dao, ModelContext<T> modelContext, ObjectMapper objectMapper) throws Exception {
        this.handleUpdate(httpContext, modelContext, objectMapper);
    }


    @SuppressWarnings("unchecked")
    private T setParamsOnObject(Request request, ObjectMapper objectMapper, T object, Class<T> modelClass) {
        Map<String, Object> map = objectMapper.convertValue(object, Map.class);

        Map<String, Object> params = new HashMap<>();
        request.queryParams().forEach(queryParam -> params.put(queryParam, request.queryParams(queryParam)));

        return MapperUtils.objectFromMaps(objectMapper, map, params, modelClass);
    }
}
