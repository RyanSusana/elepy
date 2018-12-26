package com.elepy.routes;

import com.elepy.Elepy;
import com.elepy.concepts.ObjectEvaluator;
import com.elepy.dao.Crud;
import com.fasterxml.jackson.databind.ObjectMapper;
import spark.Request;
import spark.Response;

import java.util.List;


public interface DeleteHandler<T> extends RouteHandler<T> {
    boolean delete(Request request, Response response, Crud<T> dao, ObjectMapper objectMapper);

    @Override
    default void handle(Request request, Response response, Crud<T> crud, Elepy elepy, List<ObjectEvaluator<T>> objectEvaluators, Class<T> clazz) throws Exception {
        final boolean delete = delete(request, response, crud, elepy.getObjectMapper());

        if (delete) {
            response.body("OK");
            response.status(200);
        }
    }
}
