package com.elepy.tests;

import com.elepy.dao.Crud;
import com.elepy.handlers.ActionHandler;
import com.elepy.http.HttpContext;
import com.elepy.models.ModelContext;
import com.fasterxml.jackson.databind.ObjectMapper;

public class DeleteAllAction implements ActionHandler<Product> {
    @Override
    public void handleAction(HttpContext context, Crud<Product> dao, ModelContext<Product> modelContext, ObjectMapper objectMapper) throws Exception {
        dao.delete(context.modelIds());
    }
}
