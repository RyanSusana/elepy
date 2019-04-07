package com.elepy.routes;

import com.elepy.dao.Crud;
import com.elepy.describers.ModelDescription;
import com.elepy.exceptions.ElepyException;
import com.elepy.exceptions.Message;
import com.elepy.http.HttpContext;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.Serializable;

public class DefaultDelete<T> implements DeleteHandler<T> {

    @Override
    public void handleDelete(HttpContext context, Crud<T> dao, ModelDescription<T> modelDescription, ObjectMapper objectMapper) throws Exception {
        Serializable paramId = context.modelId();

        dao.getById(paramId).orElseThrow(() -> new ElepyException(String.format("No %s found", modelDescription.getName()), 404));

        dao.deleteById(paramId);

        context.result(Message.of("Successfully deleted item", 200));
    }
}
