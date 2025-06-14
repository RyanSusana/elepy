package com.elepy.handlers;

import com.elepy.crud.Crud;
import com.elepy.exceptions.ElepyException;
import com.elepy.http.Request;
import com.elepy.http.Response;
import com.elepy.igniters.ModelDetails;
import jakarta.enterprise.context.Dependent;

import java.io.Serializable;
import java.util.Optional;

@Dependent
public class DefaultFindOne<T> implements ActionHandler<T> {


    public T findOne(Request request, Response response, Crud<T> dao, ModelDetails<T> modelDetails) {
        response.type("application/json");

        Serializable paramId = request.recordId();

        final Optional<T> id = dao.getById(paramId);
        if (id.isPresent()) {
            response.status(200);
            return id.get();

        } else {
            throw ElepyException.notFound(modelDetails.getName());
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
