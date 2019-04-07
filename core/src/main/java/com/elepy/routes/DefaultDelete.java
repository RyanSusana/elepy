package com.elepy.routes;

import com.elepy.dao.Crud;
import com.elepy.describers.ModelDescription;
import com.elepy.exceptions.ElepyException;
import com.elepy.exceptions.Message;
import com.elepy.http.HttpContext;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.Serializable;
import java.util.Set;

public class DefaultDelete<T> implements DeleteHandler<T> {

    @Override
    public void handleDelete(HttpContext context, Crud<T> dao, ModelDescription<T> modelDescription, ObjectMapper objectMapper) throws Exception {
        Set<Serializable> paramIds = context.modelIds();

        delete(paramIds, dao, context, modelDescription);
    }

    protected void delete(Set<Serializable> paramIds, Crud<T> dao, HttpContext context, ModelDescription<T> modelDescription) {
        if (paramIds.size() == 1) {
            dao.getById(paramIds.iterator().next()).orElseThrow(() -> new ElepyException(String.format("No %s found", modelDescription.getName()), 404));

            dao.deleteById(paramIds.iterator().next());

            context.result(Message.of("Successfully deleted item", 200));
        } else if (paramIds.size() > 1) {
            dao.delete(paramIds);
            context.result(Message.of("Successfully deleted items", 200));
        }
    }
}
