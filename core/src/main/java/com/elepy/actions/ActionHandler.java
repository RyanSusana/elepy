package com.elepy.actions;

import com.elepy.dao.Crud;
import com.elepy.handlers.BaseHandler;
import com.elepy.http.HttpContext;
import com.elepy.models.ModelContext;
import com.fasterxml.jackson.databind.ObjectMapper;

public interface ActionHandler<T> extends BaseHandler<T> {
    void handleAction(HttpContext context, Crud<T> dao, ModelContext<T> modelContext, ObjectMapper objectMapper) throws Exception;
}