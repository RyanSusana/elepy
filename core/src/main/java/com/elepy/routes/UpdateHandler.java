package com.elepy.routes;

import com.elepy.concepts.ObjectEvaluator;
import com.elepy.dao.Crud;
import com.elepy.di.ElepyContext;
import com.fasterxml.jackson.databind.ObjectMapper;
import spark.Request;
import spark.Response;

import java.util.List;


public interface UpdateHandler<T> extends RouteHandler<T> {
    boolean update(Request request, Response response, Crud<T> dao, Class<? extends T> clazz, ObjectMapper objectMapper, List<ObjectEvaluator<T>> objectEvaluators) throws Exception;

    @Override
    default void handle(Request request, Response response, Crud<T> crud, ElepyContext elepy, List<ObjectEvaluator<T>> objectEvaluators, Class<T> clazz) throws Exception {
        final boolean update = update(request, response, crud, clazz, elepy.getObjectMapper(), objectEvaluators);

        if (update) {
            response.body("OK");
            response.status(200);
        }
    }
}
