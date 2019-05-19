package com.elepy.routes;

import com.elepy.dao.Crud;
import com.elepy.describers.ModelContext;
import com.elepy.http.HttpContext;
import com.fasterxml.jackson.databind.ObjectMapper;

public interface ActionHandler<T> extends BaseHandler<T> {
    void handleAction(HttpContext context, Crud<T> dao, ModelContext<T> modelContext, ObjectMapper objectMapper) throws Exception;
} 
