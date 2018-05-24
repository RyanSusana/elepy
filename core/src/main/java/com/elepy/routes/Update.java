package com.elepy.routes;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.elepy.concepts.ObjectEvaluator;
import com.elepy.dao.Crud;
import spark.Request;
import spark.Response;

import java.util.List;
import java.util.Optional;


public interface Update<T> {
    Optional<T> update(Request request, Response response, Crud<T> dao, Class<? extends T> clazz, ObjectMapper objectMapper, List<ObjectEvaluator<T>> objectEvaluators) throws Exception;


}
