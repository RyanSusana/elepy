package com.elepy.admin;

import com.elepy.dao.Crud;
import com.elepy.describers.ModelContext;
import com.elepy.exceptions.Message;
import com.elepy.http.HttpContext;
import com.elepy.routes.ActionHandler;
import com.fasterxml.jackson.databind.ObjectMapper;

public class ProductFindOnGoogle implements ActionHandler<Product> {
    @Override
    public void handleAction(HttpContext context, Crud<Product> dao, ModelContext<Product> modelContext, ObjectMapper objectMapper) throws Exception {

        dao.getById(context.modelId()).ifPresent(product -> {
        });

        context.response().result(Message.of("LOL", 200));
    }
}
