package com.elepy.handlers;

import com.elepy.dao.Crud;
import com.elepy.exceptions.ElepyException;
import com.elepy.http.Request;
import com.elepy.http.Response;
import com.elepy.igniters.ModelContext;

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
            throw ElepyException.notFound(modelContext.getName());
        }
    }

    @Override
    public void handle(HandlerContext<T> ctx) throws Exception {
 final var context = ctx.http();
 final var modelContext = ctx.model();
        T object = findOne(context.request(), context.response(), ctx.crud(), modelContext);
        context.response().json(object);
    }
}
