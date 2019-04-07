package com.elepy.routes;

import com.elepy.dao.Crud;
import com.elepy.describers.ModelDescription;
import com.elepy.exceptions.ElepyException;
import com.elepy.exceptions.Message;
import com.elepy.http.HttpContext;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.Serializable;

public abstract class SimpleDelete<T> implements DeleteHandler<T> {
    @Override
    public void handleDelete(HttpContext context, Crud<T> dao, ModelDescription<T> modelDescription, ObjectMapper objectMapper) throws Exception {
        Serializable paramId = context.modelId();

        T itemToDelete = dao.getById(paramId).orElseThrow(() -> new ElepyException(String.format("No %s found", modelDescription.getName()), 404));

        beforeDelete(itemToDelete, dao);
        dao.deleteById(paramId);
        afterDelete(itemToDelete, dao);

        context.response().status(200);
        context.response().result(Message.of("Successfully deleted item", 200));
    }

    public abstract void afterDelete(T itemToDelete, Crud<T> dao);

    public abstract void beforeDelete(T deletedItem, Crud<T> dao);

}
