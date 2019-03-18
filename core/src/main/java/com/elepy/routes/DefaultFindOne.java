package com.elepy.routes;

import com.elepy.dao.Crud;
import com.elepy.describers.ModelDescription;
import com.elepy.http.HttpContext;
import com.elepy.http.Request;
import com.elepy.http.Response;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.Optional;

public class DefaultFindOne<T> implements FindOneHandler<T> {

    @Override
    public void handleFindOne(HttpContext context, Crud<T> crud, ModelDescription<T> modelDescription, ObjectMapper objectMapper) throws Exception {
        findOne(context.request(), context.response(), crud, objectMapper, modelDescription.getModelType());
    }

    private void findOne(Request request, Response response, Crud<T> dao, ObjectMapper objectMapper, Class<T> clazz) throws JsonProcessingException {
        response.type("application/json");

        Object paramId = request.modelId();

        final Optional<T> id = dao.getById(paramId);
        if (id.isPresent()) {
            response.status(200);
            response.result(objectMapper.writeValueAsString(id.get()));
        } else {
            response.status(404);
            response.result("");
        }
    }
}
