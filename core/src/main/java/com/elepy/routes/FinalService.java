package com.elepy.routes;

import com.elepy.concepts.ObjectEvaluator;
import com.elepy.dao.Crud;
import com.elepy.di.ElepyContext;
import com.elepy.http.Request;
import com.elepy.http.Response;

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
    public void handleFind(Request request, Response response, Crud<T> crud, ElepyContext elepy, List<ObjectEvaluator<T>> objectEvaluators, Class<T> clazz) throws Exception {
        find.handleFind(request, response, crud, elepy, objectEvaluators, clazz);
    }

    @Override
    public void handleCreate(Request request, Response response, Crud<T> dao, ElepyContext elepy, List<ObjectEvaluator<T>> objectEvaluators, Class<T> clazz) throws Exception {
        create.handleCreate(request, response, dao, elepy, objectEvaluators, clazz);
    }

    @Override
    public void handleDelete(Request request, Response response, Crud<T> dao, ElepyContext elepy, List<ObjectEvaluator<T>> objectEvaluators, Class<T> tClass) throws Exception {
        delete.handleDelete(request, response, dao, elepy, objectEvaluators, tClass);
    }

    @Override
    public void handleUpdate(Request request, Response response, Crud<T> dao, ElepyContext elepy, List<ObjectEvaluator<T>> objectEvaluators, Class<T> clazz) throws Exception {
        update.handleUpdate(request, response, dao, elepy, objectEvaluators, clazz);
    }

}
