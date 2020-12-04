package com.elepy.tests.basic;

import com.elepy.handlers.ActionHandler;
import com.elepy.handlers.Context;

public class ResourceExtraAction implements ActionHandler {
    @Override
    public void handle(Context ctx) throws Exception {

        ctx.http().type("application/json");
        ctx.http().response().json(ctx.http().recordIds());

    }
}
