package com.elepy.handlers;

import com.elepy.dao.Crud;
import com.elepy.http.HttpContext;
import com.elepy.http.Request;
import com.elepy.models.ModelContext;

import java.util.List;

/**
 * Use this class to map the results of a RestModel to another type.
 *
 * @param <T> The type of the RestModel
 * @param <R> The type you want map to
 */
public abstract class MappedFindMany<T, R> extends DefaultFindMany<T> {

    @Override
    public void handle(HttpContext context, ModelContext<T> modelContext) throws Exception {
        List<T> result = find(context, modelContext.getCrud());

        List<R> mappedResult = mapValues(result, context.request(), modelContext.getCrud());

        context.response().json(mappedResult);
    }

    public abstract List<R> mapValues(List<T> objectsToMap, Request request, Crud<T> crud);
}
