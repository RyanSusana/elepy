package com.elepy.handlers;

import com.elepy.dao.Crud;
import com.elepy.exceptions.ElepyException;
import com.elepy.exceptions.Message;

import java.io.Serializable;

public abstract class SimpleDelete<T> implements ActionHandler<T> {
    @Override
    public void handle(HandlerContext<T> ctx) {
 final var context = ctx.http();
 final var modelContext = ctx.model();

        if (context.recordIds().size() > 1) {
            throw new ElepyException(String.format("SimpleDelete<%s> does not support multiple id deletions", modelContext.getModelType().getSimpleName()), 400);
        }

        var crud = ctx.crud();
        Serializable paramId = context.recordId();

        T itemToDelete = crud.getById(paramId).orElseThrow(() -> new ElepyException(String.format("No %s found", modelContext.getName()), 404));

        beforeDelete(itemToDelete, crud);
        crud.deleteById(paramId);
        afterDelete(itemToDelete, crud);

        context.response().status(200);
        context.response().result(Message.of("Successfully deleted item", 200));
    }


    public abstract void beforeDelete(T itemToDelete, Crud<T> dao);
    public abstract void afterDelete(T deletedItem, Crud<T> dao);


}
