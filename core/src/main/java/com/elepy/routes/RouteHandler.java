package com.elepy.routes;

import com.elepy.Elepy;
import com.elepy.concepts.ObjectEvaluator;
import com.elepy.dao.Crud;
import com.fasterxml.jackson.databind.ObjectMapper;
import spark.Request;
import spark.Response;

import java.io.IOException;
import java.util.List;

public interface RouteHandler<T> {

    default T getModelFromRequest(Request request, ObjectMapper objectMapper, Class<T> clazz) throws IOException {

        return objectMapper.readValue(request.body(), clazz);
    }

    void handle(Request request, Response response, Crud<T> crud, Elepy elepy, List<ObjectEvaluator<T>> objectEvaluators, Class<T> clazz) throws Exception;
}
