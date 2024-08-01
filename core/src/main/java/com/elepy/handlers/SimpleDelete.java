package com.elepy.handlers;

import com.elepy.crud.Crud;
import com.elepy.exceptions.Message;

public abstract class SimpleDelete<T> implements ActionHandler<T> {
    @Override
    public void handle(HandlerContext<T> ctx) {
        final var context = ctx.http();
        final var modelContext = ctx.model();

        var crud = ctx.crud();
        final var before = crud.getByIds(ctx.http().recordIds());

        before.forEach(toDelete -> beforeDelete(toDelete, crud));

        crud.delete(ctx.http().recordIds());

        before.forEach(toDelete -> afterDelete(toDelete, crud));

        context.response().status(200);
        context.response().result(Message.of("Successfully deleted item", 200));
    }


    public abstract void beforeDelete(T itemToDelete, Crud<T> dao);

    public abstract void afterDelete(T deletedItem, Crud<T> dao);


}
