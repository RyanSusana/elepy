package com.elepy.handlers;

import com.elepy.crud.Crud;
import com.elepy.http.HttpContext;
import com.elepy.http.Request;

import java.util.List;

/**
 * Use this class to map the results of a RestModel to another type.
 *
 * @param <T> The type of the RestModel
 * @param <R> The type you want map to
 */
public abstract class MappedFindMany<T, R extends T> extends DefaultFindMany<T> {

    @Override
    public List<? extends T> find(HttpContext context, HandlerContext<T> handlerContext) {

        List<? extends T> result = super.find(context, handlerContext);

        return mapValues(result, context.request(), handlerContext.crud());

    }

    public abstract List<R> mapValues(List<? extends T> objectsToMap, Request request, Crud<T> crud);
}
