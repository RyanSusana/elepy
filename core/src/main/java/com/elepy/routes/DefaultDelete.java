package com.elepy.routes;

import com.elepy.dao.Crud;
import com.elepy.describers.ModelDescription;
import com.elepy.http.HttpContext;
import com.elepy.utils.ClassUtils;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.Optional;

public class DefaultDelete<T> implements DeleteHandler<T> {

    @Override
    public void handleDelete(HttpContext context, Crud<T> dao, ModelDescription<T> modelDescription, ObjectMapper objectMapper) throws Exception {

        Object paramId = ClassUtils.toObjectIdFromString(modelDescription.getModelType(), context.request().params("id"));

        final Optional<T> id = dao.getById(paramId);
        if (id.isPresent()) {
            dao.delete(paramId);
        }
        context.response().status(200);
        context.response().result("OK");
    }
}
