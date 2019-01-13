package com.elepy.routes;

import com.elepy.concepts.ObjectEvaluator;
import com.elepy.dao.Crud;
import com.elepy.di.ElepyContext;
import spark.Request;
import spark.Response;

import java.util.List;

public class DefaultService<T> implements FindHandler<T>, CreateHandler<T>, UpdateHandler<T>, DeleteHandler<T> {

    private DefaultFind<T> find;
    private DefaultCreate<T> create;
    private DefaultUpdate<T> update;
    private DefaultDelete<T> delete;

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
