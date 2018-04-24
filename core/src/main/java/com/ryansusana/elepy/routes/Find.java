package com.ryansusana.elepy.routes;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ryansusana.elepy.dao.Crud;
import spark.Request;
import spark.Response;

import java.util.List;

public interface Find<T> {
    List<T> find(Request request, Response response, Crud<T> dao, ObjectMapper objectMapper);
}
