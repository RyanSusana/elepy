package com.elepy.routes;

import com.elepy.concepts.ObjectEvaluator;
import com.elepy.dao.Crud;
import com.elepy.di.ElepyContext;
import com.elepy.http.HttpContext;

import java.util.List;
import java.util.Optional;

public class DefaultDelete<T> implements DeleteHandler<T> {

    @Override
    public void handleDelete(HttpContext context, Crud<T> dao, ElepyContext elepy, List<ObjectEvaluator<T>> objectEvaluators, Class<T> tClass) throws Exception {
        final Optional<T> id = dao.getById(context.request().params("id"));
        if (id.isPresent()) {
            dao.delete(context.request().params("id"));
        }
        context.response().status(200);
        context.response().result("OK");
    }
}
