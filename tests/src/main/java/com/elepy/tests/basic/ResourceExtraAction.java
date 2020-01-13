package com.elepy.tests.basic;

import com.elepy.dao.Crud;
import com.elepy.handlers.ActionHandler;
import com.elepy.http.HttpContext;
import com.elepy.models.ModelContext;
import com.fasterxml.jackson.databind.ObjectMapper;

public class ResourceExtraAction implements ActionHandler {
    @Override
    public void handle(HttpContext context,  ModelContext modelContext) throws Exception {

        context.type("application/json");
        context.response().json(context.recordIds());

    }
}
