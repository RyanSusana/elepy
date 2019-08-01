package com.elepy.handlers;

import com.elepy.dao.Crud;
import com.elepy.http.HttpContext;
import com.elepy.http.Request;
import com.elepy.models.ModelContext;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Use this class to map the result of a RestModel to another type.
 *
 * @param <T> The type of the RestModel
 * @param <R> The type you want map to
 */
public abstract class MappedFindOne<T, R> extends DefaultFindOne<T> {

    @Override
    public void handleFindOne(HttpContext context, Crud<T> crud, ModelContext<T> modelContext, ObjectMapper objectMapper) throws Exception {
        T object = findOne(context.request(), context.response(), crud, modelContext);

        R mapped = map(object, context.request(), crud);

        context.response().result(objectMapper.writeValueAsString(mapped));
    }

    public abstract R map(T objectToMap, Request request, Crud<T> crud);

}
