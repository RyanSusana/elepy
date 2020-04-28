package com.elepy.handlers;

import com.elepy.dao.Crud;
import com.elepy.http.HttpContext;
import com.elepy.models.ModelContext;

import java.util.List;

public class DefaultFindMany<T> implements ActionHandler<T> {


    public List<? extends T> find(HttpContext context, Crud<T> dao) {

        context.type("application/json");

        if (context.queryParams("ids") != null) {
            return dao.getByIds(context.request().recordIds());
        } else {

            return dao.find(context.request().parseQuery());
        }
    }

    public long count(HttpContext context, Crud<T> dao) {
        return dao.count(context.request().parseQuery());
    }


    @Override
    public final void handle(HttpContext context, ModelContext<T> modelContext) throws Exception {

        context.status(200);
        if (context.queryParams("count") != null) {
            context.response().json(count(context, modelContext.getCrud()));
        } else {
            context.response().json(find(context, modelContext.getCrud()));
        }
    }
}
