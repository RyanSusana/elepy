package com.elepy.routes;

import com.elepy.concepts.ObjectEvaluator;
import com.elepy.dao.Crud;
import com.elepy.di.ElepyContext;
import com.elepy.http.HttpContext;

import java.util.List;

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
    public void handleFind(HttpContext context, Crud<T> crud, ElepyContext elepy, List<ObjectEvaluator<T>> objectEvaluators, Class<T> clazz) throws Exception {
        find.handleFind(context, crud, elepy, objectEvaluators, clazz);
    }

    @Override
    public void handleCreate(HttpContext context, Crud<T> dao, ElepyContext elepy, List<ObjectEvaluator<T>> objectEvaluators, Class<T> clazz) throws Exception {
        create.handleCreate(context, dao, elepy, objectEvaluators, clazz);
    }

    @Override
    public void handleDelete(HttpContext context, Crud<T> dao, ElepyContext elepy, List<ObjectEvaluator<T>> objectEvaluators, Class<T> tClass) throws Exception {
        delete.handleDelete(context, dao, elepy, objectEvaluators, tClass);
    }

    @Override
    public void handleUpdate(HttpContext context, Crud<T> dao, ElepyContext elepy, List<ObjectEvaluator<T>> objectEvaluators, Class<T> clazz) throws Exception {
        update.handleUpdate(context, dao, elepy, objectEvaluators, clazz);
    }

}
