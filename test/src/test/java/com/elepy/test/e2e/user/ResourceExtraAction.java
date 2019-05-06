package com.elepy.test.e2e.user;

import com.elepy.dao.Crud;
import com.elepy.describers.ModelContext;
import com.elepy.http.HttpContext;
import com.elepy.routes.ActionHandler;
import com.fasterxml.jackson.databind.ObjectMapper;

public class ResourceExtraAction implements ActionHandler {
    @Override
    public void handleAction(HttpContext context, Crud dao, ModelContext modelContext, ObjectMapper objectMapper) throws Exception {

        context.type("application/json");
        context.result(objectMapper.writeValueAsString(context.modelIds()));

    }
}
