package com.elepy.handlers;

import com.elepy.dao.Crud;
import com.elepy.exceptions.ElepyException;
import com.elepy.http.HttpContext;
import com.elepy.models.ModelContext;
import com.fasterxml.jackson.databind.ObjectMapper;

public final class DisabledHandler<T> implements ActionHandler<T>, ServiceHandler<T> {

    @Override
    public void handleAction(HttpContext context, Crud<T> dao, ModelContext<T> modelContext, ObjectMapper objectMapper) throws Exception {
        block();
    }

    @Override
    public void handleCreate(HttpContext context, Crud<T> crud, ModelContext<T> modelContext, ObjectMapper objectMapper) {
        block();
    }

    @Override
    public void handleDelete(HttpContext context, Crud<T> crud, ModelContext<T> modelContext, ObjectMapper objectMapper) {
        block();
    }

    @Override
    public void handleFindMany(HttpContext context, Crud<T> crud, ModelContext<T> modelContext, ObjectMapper objectMapper) {
        block();
    }

    @Override
    public void handleFindOne(HttpContext context, Crud<T> crud, ModelContext<T> modelContext, ObjectMapper objectMapper) {
        block();
    }

    @Override
    public void handleUpdatePut(HttpContext context, Crud<T> crud, ModelContext<T> modelContext, ObjectMapper objectMapper) {
        block();
    }

    @Override
    public void handleUpdatePatch(HttpContext context, Crud<T> crud, ModelContext<T> modelContext, ObjectMapper objectMapper) throws Exception {
        block();
    }

    private void block() {
        throw new ElepyException("Not found", 404);
    }
}
