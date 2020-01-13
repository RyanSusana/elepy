package com.elepy.tests;

import com.elepy.handlers.ActionHandler;
import com.elepy.http.HttpContext;
import com.elepy.models.ModelContext;

public class DeleteAllAction implements ActionHandler<Product> {
    @Override
    public void handle(HttpContext context, ModelContext<Product> modelContext) throws Exception {
        modelContext.getCrud().delete(context.recordIds());
    }
}
