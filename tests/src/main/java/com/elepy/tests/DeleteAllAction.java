package com.elepy.tests;

import com.elepy.handlers.ActionHandler;
import com.elepy.handlers.Context;
import com.elepy.http.HttpContext;
import com.elepy.models.ModelContext;

public class DeleteAllAction implements ActionHandler<Product> {
    @Override
    public void handle(Context<Product> ctx) throws Exception {
        ctx.crud().delete(ctx.http().recordIds());
    }
}
