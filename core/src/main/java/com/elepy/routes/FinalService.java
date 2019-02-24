package com.elepy.routes;

import com.elepy.dao.Crud;
import com.elepy.describers.ModelDescription;
import com.elepy.http.HttpContext;
import com.fasterxml.jackson.databind.ObjectMapper;

public class FinalService<T> implements ServiceHandler<T> {

    private final FindHandler<T> find;
    private final CreateHandler<T> create;
    private final UpdateHandler<T> update;
    private final DeleteHandler<T> delete;


    FinalService(FindHandler<T> find, CreateHandler<T> create, UpdateHandler<T> update, DeleteHandler<T> delete) {
        this.find = find;
        this.create = create;
        this.update = update;
        this.delete = delete;
    }

    @Override
    public void handleFindMany(HttpContext context, Crud<T> crud, ModelDescription<T> modelDescription, ObjectMapper objectMapper) throws Exception {
        find.handleFindMany(context, crud, modelDescription, objectMapper);
    }

    @Override
    public void handleFindOne(HttpContext context, Crud<T> crud, ModelDescription<T> modelDescription, ObjectMapper objectMapper) throws Exception {
        find.handleFindOne(context, crud, modelDescription, objectMapper);
    }

    @Override
    public void handleCreate(HttpContext context, Crud<T> dao, ModelDescription<T> modelDescription, ObjectMapper objectMapper) throws Exception {
        create.handleCreate(context, dao, modelDescription, objectMapper);
    }

    @Override
    public void handleDelete(HttpContext context, Crud<T> dao, ModelDescription<T> modelDescription, ObjectMapper objectMapper) throws Exception {
        delete.handleDelete(context, dao, modelDescription, objectMapper);
    }

    @Override
    public void handleUpdatePut(HttpContext context, Crud<T> dao, ModelDescription<T> modelDescription, ObjectMapper objectMapper) throws Exception {
        update.handleUpdatePut(context, dao, modelDescription, objectMapper);
    }

    @Override
    public void handleUpdatePatch(HttpContext context, Crud<T> crud, ModelDescription<T> modelDescription, ObjectMapper objectMapper) throws Exception {
        update.handleUpdatePatch(context, crud, modelDescription, objectMapper);
    }

}
