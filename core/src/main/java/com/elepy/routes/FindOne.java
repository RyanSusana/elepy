package com.elepy.routes;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.elepy.dao.Crud;
import spark.Request;
import spark.Response;

import java.util.Optional;

public interface FindOne<T> {

    Optional<T> findOne(Request request, Response response, Crud<T> dao, ObjectMapper objectMapper);
}
