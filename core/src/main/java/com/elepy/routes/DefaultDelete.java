package com.elepy.routes;

import com.elepy.dao.Crud;
import com.fasterxml.jackson.databind.ObjectMapper;
import spark.Request;
import spark.Response;

import java.util.Optional;

public class DefaultDelete<T> implements DeleteHandler<T> {

    @Override
    public boolean delete(Request request, Response response, Crud<T> dao, ObjectMapper objectMapper) {

        final Optional<T> id = dao.getById(request.params("id"));
        if (id.isPresent()) {
            dao.delete(request.params("id"));
        }
        return true;
    }
}
