package com.elepy.mongo.fast;

import com.elepy.handlers.ActionHandler;
import com.elepy.http.HttpContext;
import com.elepy.models.ModelContext;

public class ResourceExtraAction implements ActionHandler {
    @Override
    public void handle(HttpContext context, ModelContext modelContext) throws Exception {

        context.type("application/json");
        context.response().json(context.recordIds());

    }
}
