package com.elepy.handlers;

import com.elepy.dao.Crud;
import com.elepy.dao.Page;
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
        Page<T> page = find(context.request(), context.response(), modelContext.getCrud(), modelContext);

        List<R> filteredValues = mapValues(page.getValues(), context.request(), modelContext.getCrud());

        Page<R> filteredPage = new Page<>(page.getCurrentPageNumber(), page.getLastPageNumber(), filteredValues);

        context.response().json(filteredPage);
    }

    public abstract List<R> mapValues(List<T> objectsToMap, Request request, Crud<T> crud);
}
