package com.elepy.handlers;

import com.elepy.dao.Crud;
import com.elepy.exceptions.ElepyException;
import com.elepy.http.HttpContext;
import com.elepy.http.Request;
import com.elepy.http.Response;
import com.elepy.models.ModelContext;

import java.io.Serializable;
import java.util.Optional;

public class DefaultFindOne<T> implements ActionHandler<T> {


    public T findOne(Request request, Response response, Crud<T> dao, ModelContext<T> modelContext) {
        response.type("application/json");

        Serializable paramId = request.recordId();

        final Optional<T> id = dao.getById(paramId);
        if (id.isPresent()) {
            response.status(200);
            return id.get();

        } else {
            throw new ElepyException(String.format("No %s found", modelContext.getName()), 404);
        }
    }

    @Override
    public void handle(HttpContext context, ModelContext<T> modelContext) throws Exception {
        T object = findOne(context.request(), context.response(), modelContext.getCrud(), modelContext);
        context.response().json(object);
    }
}
