package com.elepy.routes;

import com.elepy.Elepy;
import com.elepy.concepts.ObjectEvaluator;
import com.elepy.dao.Crud;
import com.fasterxml.jackson.databind.ObjectMapper;
import spark.Request;
import spark.Response;

import java.util.List;

public interface CreateHandler<T> extends RouteHandler<T> {

    boolean create(Request request, Response response, Crud<T> dao, ObjectMapper objectMapper, List<ObjectEvaluator<T>> objectEvaluators) throws Exception;


    @Override
    default void handle(Request request, Response response, Crud<T> crud, Elepy elepy, List<ObjectEvaluator<T>> objectEvaluators, Class<T> clazz) throws Exception {
        final boolean create = create(request, response, crud, elepy.getObjectMapper(), objectEvaluators);
        if (create) {
            response.body("OK");
            response.status(200);
        }
    }
}
