package com.elepy.handlers;

import com.elepy.dao.Crud;
import com.elepy.exceptions.ElepyException;
import com.elepy.http.HttpContext;
import com.elepy.http.Request;
import com.elepy.http.Response;
import com.elepy.models.ModelContext;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.Serializable;
import java.util.Optional;

public class DefaultFindOne<T> implements FindOneHandler<T> {

    @Override
    public void handleFindOne(HttpContext context, Crud<T> crud, ModelContext<T> modelContext, ObjectMapper objectMapper) throws Exception {
        T object = findOne(context.request(), context.response(), crud, modelContext);
        context.response().result(objectMapper.writeValueAsString(object));
    }

    public T findOne(Request request, Response response, Crud<T> dao, ModelContext<T> modelContext) {
        response.type("application/json");

        Serializable paramId = request.modelId();

        final Optional<T> id = dao.getById(paramId);
        if (id.isPresent()) {
            response.status(200);
            return id.get();

        } else {
            throw new ElepyException(String.format("No %s found", modelContext.getName()), 404);
        }
    }
}
