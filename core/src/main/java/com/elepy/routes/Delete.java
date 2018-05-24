package com.elepy.routes;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.elepy.dao.Crud;
import spark.Request;
import spark.Response;

import java.util.Optional;


public interface Delete<T> {
    Optional<T> delete(Request request, Response response, Crud<T> dao, ObjectMapper objectMapper);
}
