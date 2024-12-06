package com.elepy.handlers;

import com.elepy.crud.Crud;
import com.elepy.http.HttpContext;

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
        return dao.count(context.request().parseQuery().getExpression());
    }


    @Override
    public void handle(HandlerContext<T> ctx) throws Exception {
        final var context = ctx.http();
        final var modelContext = ctx.model();

        context.status(200);
        if (context.queryParams("count") != null) {
            context.response().json(count(context, ctx.crud()));
        } else {
            context.response().json(find(context, ctx.crud()));
        }
    }
}
