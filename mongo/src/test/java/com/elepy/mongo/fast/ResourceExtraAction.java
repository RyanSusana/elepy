package com.elepy.mongo.fast;

import com.elepy.dao.Crud;
import com.elepy.handlers.ActionHandler;
import com.elepy.http.HttpContext;
import com.elepy.models.ModelContext;
import com.fasterxml.jackson.databind.ObjectMapper;

public class ResourceExtraAction implements ActionHandler {
    @Override
    public void handleAction(HttpContext context, Crud dao, ModelContext modelContext, ObjectMapper objectMapper) throws Exception {

        context.type("application/json");
        context.result(objectMapper.writeValueAsString(context.recordIds()));

    }
}
