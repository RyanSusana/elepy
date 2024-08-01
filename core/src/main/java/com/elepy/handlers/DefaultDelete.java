package com.elepy.handlers;

import com.elepy.crud.Crud;
import com.elepy.exceptions.ElepyException;
import com.elepy.exceptions.Message;
import com.elepy.http.HttpContext;
import com.elepy.igniters.ModelContext;

import java.io.Serializable;
import java.util.Set;

public class DefaultDelete<T> implements ActionHandler<T> {


    protected void delete(Set<Serializable> paramIds, Crud<T> dao, HttpContext context, ModelContext<T> modelContext) {
        if (paramIds.size() == 1) {
            dao.getById(paramIds.iterator().next()).orElseThrow(() -> ElepyException.notFound(modelContext.getName()));

            dao.deleteById(paramIds.iterator().next());

            context.result(Message.of("Successfully deleted item", 200));
        } else if (paramIds.size() > 1) {
            dao.delete(paramIds);
            context.result(Message.of("Successfully deleted items", 200));
        }
    }

    @Override
    public void handle(HandlerContext<T> ctx) throws Exception {
        Set<Serializable> paramIds = ctx.http().recordIds();

        delete(paramIds, ctx.crud(), ctx.http(), ctx.model());
    }
}
