package com.elepy.routes;

import com.elepy.concepts.ObjectEvaluator;
import com.elepy.dao.Crud;
import com.elepy.di.ElepyContext;
import spark.Request;
import spark.Response;

import java.util.List;

public interface RouteHandler<T> {

    void handle(Request request, Response response, Crud<T> crud, ElepyContext elepy, List<ObjectEvaluator<T>> objectEvaluators, Class<T> clazz) throws Exception;
}
