package com.elepy.tests;

import com.elepy.handlers.ActionHandler;
import com.elepy.handlers.HandlerContext;

public class DeleteAllAction implements ActionHandler<Product> {
    @Override
    public void handle(HandlerContext<Product> ctx) throws Exception {
        ctx.crud().delete(ctx.http().recordIds());
    }
}
