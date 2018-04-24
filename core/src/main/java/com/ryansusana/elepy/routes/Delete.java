package com.ryansusana.elepy.routes;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ryansusana.elepy.dao.Crud;
import spark.Request;
import spark.Response;


public interface Delete<T> {
    boolean delete(Request request, Response response, Crud<T> dao, ObjectMapper objectMapper);
}
