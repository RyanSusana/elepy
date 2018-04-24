package com.ryansusana.elepy.routes;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ryansusana.elepy.dao.Crud;
import spark.Request;
import spark.Response;

import java.util.Optional;

public class FindOneImpl<T> implements FindOne<T> {


    public FindOneImpl() {
    }

    @Override
    public Optional<T> findOne(Request request, Response response, Crud<T> dao, ObjectMapper objectMapper) {
        response.type("application/json");
        final Optional<T> model = dao.getById(request.params("id"));

        return model;

    }
}