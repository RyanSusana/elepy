package com.ryansusana.elepy.routes;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ryansusana.elepy.dao.Crud;
import com.ryansusana.elepy.dao.Page;
import spark.Request;
import spark.Response;

public interface Find<T> {
    Page<T> find(Request request, Response response, Crud<T> dao, ObjectMapper objectMapper);
}
