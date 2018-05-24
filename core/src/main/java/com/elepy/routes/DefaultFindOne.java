package com.elepy.routes;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.elepy.dao.Crud;
import spark.Request;
import spark.Response;

import java.util.Optional;

public class DefaultFindOne<T> implements FindOne<T> {


    public DefaultFindOne() {
    }

    @Override
    public Optional<T> findOne(Request request, Response response, Crud<T> dao, ObjectMapper objectMapper) {
        response.type("application/json");
        final Optional<T> model = dao.getById(request.params("id"));

        return model;

    }
}