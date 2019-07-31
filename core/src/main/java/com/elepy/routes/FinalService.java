package com.elepy.routes;

import com.elepy.dao.Crud;
import com.elepy.http.HttpContext;
import com.elepy.models.ModelContext;
import com.fasterxml.jackson.databind.ObjectMapper;

public class FinalService<T> implements ServiceHandler<T> {

    private final FindManyHandler<T> findMany;
    private final FindOneHandler<T> findOne;
    private final CreateHandler<T> create;
    private final UpdateHandler<T> update;
    private final DeleteHandler<T> delete;


    FinalService(FindManyHandler<T> findMany, FindOneHandler<T> findOne, CreateHandler<T> create, UpdateHandler<T> update, DeleteHandler<T> delete) {
        this.findMany = findMany;
        this.findOne = findOne;
        this.create = create;
        this.update = update;
        this.delete = delete;
    }

    @Override
    public void handleFindMany(HttpContext context, Crud<T> crud, ModelContext<T> modelContext, ObjectMapper objectMapper) throws Exception {
        findMany.handleFindMany(context, crud, modelContext, objectMapper);
    }

    @Override
    public void handleFindOne(HttpContext context, Crud<T> crud, ModelContext<T> modelContext, ObjectMapper objectMapper) throws Exception {
        findOne.handleFindOne(context, crud, modelContext, objectMapper);
    }

    @Override
    public void handleCreate(HttpContext context, Crud<T> dao, ModelContext<T> modelContext, ObjectMapper objectMapper) throws Exception {
        create.handleCreate(context, dao, modelContext, objectMapper);
    }

    @Override
    public void handleDelete(HttpContext context, Crud<T> dao, ModelContext<T> modelContext, ObjectMapper objectMapper) throws Exception {
        delete.handleDelete(context, dao, modelContext, objectMapper);
    }

    @Override
    public void handleUpdatePut(HttpContext context, Crud<T> dao, ModelContext<T> modelContext, ObjectMapper objectMapper) throws Exception {
        update.handleUpdatePut(context, dao, modelContext, objectMapper);
    }

    @Override
    public void handleUpdatePatch(HttpContext context, Crud<T> crud, ModelContext<T> modelContext, ObjectMapper objectMapper) throws Exception {
        update.handleUpdatePatch(context, crud, modelContext, objectMapper);
    }

}
