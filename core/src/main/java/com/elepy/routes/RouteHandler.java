package com.elepy.routes;

import com.elepy.Elepy;
import com.elepy.concepts.ObjectEvaluator;
import com.elepy.dao.Crud;
import com.elepy.exceptions.RestErrorMessage;
import com.fasterxml.jackson.databind.ObjectMapper;
import spark.Request;
import spark.Response;

import java.util.List;

public interface RouteHandler<T> {
    void handle(Request request, Response response, Crud<T> crud, Elepy elepy, List<ObjectEvaluator<T>> objectEvaluators, Class<T> clazz) throws Exception;
}
