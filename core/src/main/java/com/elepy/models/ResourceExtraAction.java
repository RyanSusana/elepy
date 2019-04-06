package com.elepy.models;

import com.elepy.dao.Crud;
import com.elepy.describers.ModelDescription;
import com.elepy.http.HttpContext;
import com.elepy.routes.ActionHandler;
import com.fasterxml.jackson.databind.ObjectMapper;

public class ResourceExtraAction implements ActionHandler {
    @Override
    public void handleAction(HttpContext context, Crud dao, ModelDescription modelDescription, ObjectMapper objectMapper) throws Exception {

        context.type("application/json");
        context.result(objectMapper.writeValueAsString(context.modelIds()));

    }
}
