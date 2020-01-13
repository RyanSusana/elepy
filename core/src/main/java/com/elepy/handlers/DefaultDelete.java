package com.elepy.handlers;

import com.elepy.dao.Crud;
import com.elepy.exceptions.ElepyException;
import com.elepy.exceptions.Message;
import com.elepy.http.HttpContext;
import com.elepy.models.ModelContext;

import java.io.Serializable;
import java.util.Set;

public class DefaultDelete<T> implements ActionHandler<T> {


    protected void delete(Set<Serializable> paramIds, Crud<T> dao, HttpContext context, ModelContext<T> modelContext) {
        if (paramIds.size() == 1) {
            dao.getById(paramIds.iterator().next()).orElseThrow(() -> new ElepyException(String.format("No %s found", modelContext.getName()), 404));

            dao.deleteById(paramIds.iterator().next());

            context.result(Message.of("Successfully deleted item", 200));
        } else if (paramIds.size() > 1) {
            dao.delete(paramIds);
            context.result(Message.of("Successfully deleted items", 200));
        }
    }

    @Override
    public void handle(HttpContext context, ModelContext<T> modelContext) throws Exception {
        Set<Serializable> paramIds = context.recordIds();

        delete(paramIds, modelContext.getCrud(), context, modelContext);
    }
}
