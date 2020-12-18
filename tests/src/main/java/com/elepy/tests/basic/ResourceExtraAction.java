package com.elepy.tests.basic;

import com.elepy.handlers.ActionHandler;
import com.elepy.handlers.HandlerContext;

public class ResourceExtraAction implements ActionHandler {
    @Override
    public void handle(HandlerContext ctx) throws Exception {

        ctx.http().type("application/json");
        ctx.http().response().json(ctx.http().recordIds());

    }
}
