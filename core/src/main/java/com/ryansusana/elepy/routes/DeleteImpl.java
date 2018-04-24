package com.ryansusana.elepy.routes;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ryansusana.elepy.dao.Crud;
import spark.Request;
import spark.Response;

public class DeleteImpl<T> implements Delete<T> {

    public DeleteImpl() {
    }

    @Override
    public boolean delete(Request request, Response response, Crud<T> dao, ObjectMapper objectMapper) {
        dao.delete(request.params("id"));
        return !dao.getById(request.params("id")).isPresent();

    }
}
