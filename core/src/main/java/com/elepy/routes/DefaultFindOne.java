package com.elepy.routes;

import com.elepy.dao.Crud;
import com.elepy.describers.ModelDescription;
import com.elepy.exceptions.ElepyException;
import com.elepy.http.HttpContext;
import com.elepy.http.Request;
import com.elepy.http.Response;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.Optional;

public class DefaultFindOne<T> implements FindOneHandler<T> {

    @Override
    public void handleFindOne(HttpContext context, Crud<T> crud, ModelDescription<T> modelDescription, ObjectMapper objectMapper) throws Exception {
        T object = findOne(context.request(), context.response(), crud, objectMapper, modelDescription);
        context.response().result(objectMapper.writeValueAsString(object));
    }

    public T findOne(Request request, Response response, Crud<T> dao, ObjectMapper objectMapper, ModelDescription<T> modelDescription) throws JsonProcessingException {
        response.type("application/json");

        Object paramId = request.modelId();

        final Optional<T> id = dao.getById(paramId);
        if (id.isPresent()) {
            response.status(200);
            return id.get();

        } else {
            throw new ElepyException(String.format("No %s found", modelDescription.getName()), 404);
        }
    }
}
