package com.elepy.handlers;

import com.elepy.annotations.Uneditable;
import com.elepy.evaluators.DefaultIntegrityEvaluator;
import com.elepy.evaluators.EvaluationType;
import com.elepy.exceptions.ElepyException;
import com.elepy.exceptions.Message;
import com.elepy.http.Request;
import com.elepy.schemas.FieldType;
import com.elepy.schemas.Schema;
import com.elepy.utils.ReflectionUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.enterprise.context.Dependent;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Dependent
public class DefaultUpdate<T> implements ActionHandler<T> {


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
                return combineMapsIntoNewObject(objectMapper, beforeMap, changesMap, schema.getJavaClass());
            } else {
                return setParamsOnObject(request, objectMapper, before, schema.getJavaClass());
            }
        }
    }

    public T handleUpdate(HandlerContext<T> ctx, ObjectMapper objectMapper) throws Exception {
        final var context = ctx.http();
        final var modelContext = ctx.model();
        String body = context.body();

        if (body == null || body.isEmpty()) {
            throw ElepyException.translated("{elepy.messages.exceptions.errorParsingJson}");
        }

        T before = ctx.crud().getById(context.recordId()).orElseThrow(() -> ElepyException.notFound("Object"));
        final T updated = updatedObjectFromRequest(before, context.request(), objectMapper, modelContext.getSchema());

        evaluateAndUpdate(ctx, updated);

        context.result(Message.of("Successfully updated item", 200));
        return updated;
    }

    public T evaluateAndUpdate(HandlerContext<T> ctx, T update) throws Exception {
        ctx.http().validate(update);

        new DefaultIntegrityEvaluator<>(ctx.model().getCrud()).evaluate(update, EvaluationType.UPDATE);
        ctx.crud().update(update);

        return update;
    }


    @SuppressWarnings("unchecked")
    private T setParamsOnObject(Request request, ObjectMapper objectMapper, T object, Class<T> schemaClass) {
        Map<String, Object> map = objectMapper.convertValue(object, Map.class);

        Map<String, Object> params = new HashMap<>();
        request.queryParams().forEach(queryParam -> params.put(queryParam, request.queryParams(queryParam)));

        return combineMapsIntoNewObject(objectMapper, map, params, schemaClass);
    }


    /**
     * This method combines two maps into a new object. It will only add fields that are not marked as uneditable.
     *
     * @param objectMapper the Jackson ObjectMapper
     * @param objectAsMap the object as a map
     * @param fieldsToAdd the fields to add to the object
     * @param cls the class of the object
     * @return the new object
     * @param <T> the type of the object
     */
    public static <T> T combineMapsIntoNewObject(ObjectMapper objectMapper, Map<String, Object> objectAsMap, Map<String, Object> fieldsToAdd, Class<T> cls) {
        final Field idProperty = ReflectionUtils.getIdField(cls).orElseThrow(ElepyException::internalServerError);
        fieldsToAdd.forEach((fieldName, fieldObject) -> {
            final Field field = ReflectionUtils.findFieldWithName(cls, fieldName).orElseThrow(() -> ElepyException.translated("{elepy.messages.exceptions.unknownProperty}", fieldName));
            FieldType fieldType = FieldType.guessFieldType(field);
            if (fieldType.isPrimitive() && !idProperty.getName().equals(field.getName()) && shouldEdit(field)) {
                objectAsMap.put(fieldName, fieldObject);
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
    @Override
    public void handle(HandlerContext<T> ctx) throws Exception {
        final var context = ctx.http();
        this.handleUpdate(ctx, context.elepy().objectMapper());
    }
}
