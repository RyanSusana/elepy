package com.elepy.handlers;

import com.elepy.dao.Crud;
import com.elepy.exceptions.ElepyException;
import com.elepy.exceptions.Message;
import com.elepy.http.HttpContext;
import com.elepy.models.ModelContext;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.Serializable;

public abstract class SimpleDelete<T> implements DeleteHandler<T> {
    @Override
    public void handleDelete(HttpContext context, Crud<T> dao, ModelContext<T> modelContext, ObjectMapper objectMapper) throws Exception {

        if (context.recordIds().size() > 1) {
            throw new ElepyException(String.format("SimpleDelete<%s> does not support multiple id deletions", modelContext.getModelType().getSimpleName()), 400);
        }

        Serializable paramId = context.recordId();

        T itemToDelete = dao.getById(paramId).orElseThrow(() -> new ElepyException(String.format("No %s found", modelContext.getName()), 404));

        beforeDelete(itemToDelete, dao);
        dao.deleteById(paramId);
        afterDelete(itemToDelete, dao);

        context.response().status(200);
        context.response().result(Message.of("Successfully deleted item", 200));
    }

    public abstract void afterDelete(T deletedItem, Crud<T> dao);

    public abstract void beforeDelete(T itemToDelete, Crud<T> dao);

}
