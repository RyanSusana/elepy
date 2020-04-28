package com.elepy.handlers;

import com.elepy.dao.Crud;
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
    public List<? extends T> find(HttpContext context, Crud<T> dao) {

        List<? extends T> result = super.find(context, dao);

        return mapValues(result, context.request(), dao);

    }

    public abstract List<R> mapValues(List<? extends T> objectsToMap, Request request, Crud<T> crud);
}
