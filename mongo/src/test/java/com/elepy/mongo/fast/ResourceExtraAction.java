package com.elepy.mongo.fast;

import com.elepy.handlers.ActionHandler;
import com.elepy.handlers.Context;
import com.elepy.http.HttpContext;
import com.elepy.models.ModelContext;

public class ResourceExtraAction implements ActionHandler {
    @Override
    public void handle(Context ctx) throws Exception {

        ctx.http().type("application/json");
        ctx.http().response().json(ctx.http().recordIds());

    }
}
