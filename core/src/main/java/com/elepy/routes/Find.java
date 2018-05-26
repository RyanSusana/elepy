package com.elepy.routes;

import com.elepy.dao.Crud;
import com.elepy.dao.Page;
import com.fasterxml.jackson.databind.ObjectMapper;
import spark.Request;
import spark.Response;

public interface Find<T> {
    Page<T> find(Request request, Response response, Crud<T> dao, ObjectMapper objectMapper);
}
