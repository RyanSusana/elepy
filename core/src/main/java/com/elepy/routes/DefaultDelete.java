package com.elepy.routes;

import com.elepy.concepts.ObjectEvaluator;
import com.elepy.dao.Crud;
import com.elepy.di.ElepyContext;
import spark.Request;
import spark.Response;

import java.util.List;
import java.util.Optional;

public class DefaultDelete<T> implements RouteHandler<T> {

    @Override
    public void handle(Request request, Response response, Crud<T> dao, ElepyContext elepy, List<ObjectEvaluator<T>> objectEvaluators, Class<T> tClass) throws Exception {
        final Optional<T> id = dao.getById(request.params("id"));
        if (id.isPresent()) {
            dao.delete(request.params("id"));
        }
        response.status(200);
        response.body("OK");
    }
}
