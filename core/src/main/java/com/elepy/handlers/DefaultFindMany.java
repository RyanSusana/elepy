package com.elepy.handlers;

import com.elepy.dao.Crud;
import com.elepy.http.HttpContext;
import com.elepy.models.ModelContext;

import java.util.Collection;
import java.util.List;

public class DefaultFindMany<T> implements ActionHandler<T> {


    public List<T> find(HttpContext context, Crud<T> dao) {

        context.type("application/json");

        if (context.queryParams("ids") != null) {
            return dao.getByIds(context.request().recordIds());
        } else {
            context.status(200);

            return dao.find(context.request().parseQuery());
        }
    }


    @Override
    public void handle(HttpContext context, ModelContext<T> modelContext) throws Exception {

        if (context.queryParams("count") != null) {
            context.status(200);
            context.response().json(modelContext.getCrud().count(context.request().parseQuery()));
        } else {
            Collection<T> result = find(context, modelContext.getCrud());
            context.response().json(result);
        }
    }
}
