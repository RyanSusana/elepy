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
    public void handle(Context<T> ctx) throws Exception {
 final var context = ctx.http();
 final var modelContext = ctx.model();
        T object = findOne(context.request(), context.response(), ctx.crud(), modelContext);

        R mapped = map(object, context.request(), ctx.crud());

        context.response().json(mapped);
    }

    public abstract R map(T objectToMap, Request request, Crud<T> crud);

}
