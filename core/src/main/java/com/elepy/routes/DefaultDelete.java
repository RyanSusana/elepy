package com.elepy.routes;

import com.elepy.concepts.ObjectEvaluator;
import com.elepy.dao.Crud;
import com.elepy.di.ElepyContext;
import com.elepy.http.HttpContext;
import com.elepy.utils.ClassUtils;

import java.util.List;
import java.util.Optional;

public class DefaultDelete<T> implements DeleteHandler<T> {

    @Override
    public void handleDelete(HttpContext context, Crud<T> dao, ElepyContext elepy, List<ObjectEvaluator<T>> objectEvaluators, Class<T> tClass) throws Exception {

        Object paramId = ClassUtils.toObjectIdFromString(tClass, context.request().params("id"));

        final Optional<T> id = dao.getById(paramId);
        if (id.isPresent()) {
            dao.delete(paramId);
        }
        context.response().status(200);
        context.response().result("OK");
    }
}
