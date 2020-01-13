package com.elepy.handlers;

import com.elepy.dao.Crud;
import com.elepy.http.HttpContext;
import com.elepy.http.Request;
import com.elepy.models.ModelContext;

/**
 * Use this class to map the result of a RestModel to another type.
 *
 * @param <T> The type of the RestModel
 * @param <R> The type you want map to
 */
public abstract class MappedFindOne<T, R> extends DefaultFindOne<T> {

    @Override
    public void handle(HttpContext context, ModelContext<T> modelContext) throws Exception {
        T object = findOne(context.request(), context.response(), modelContext.getCrud(), modelContext);

        R mapped = map(object, context.request(), modelContext.getCrud());

        context.response().json(mapped);
    }

    public abstract R map(T objectToMap, Request request, Crud<T> crud);

}
