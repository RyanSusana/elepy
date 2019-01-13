package com.elepy.models;

import com.elepy.annotations.Inject;
import com.elepy.concepts.ObjectEvaluator;
import com.elepy.concepts.Resource;
import com.elepy.dao.Crud;
import com.elepy.dao.jongo.MongoDao;
import com.elepy.di.ElepyContext;
import com.elepy.routes.DefaultService;
import spark.Request;
import spark.Response;

import java.util.List;

public class ResourceService extends DefaultService<Resource> {

    @Inject(tag = "/resources")
    private MongoDao<Resource> crud;

    @Override
    public void handleFind(Request request, Response response, Crud<Resource> crud, ElepyContext elepy, List<ObjectEvaluator<Resource>> objectEvaluators, Class<Resource> clazz) throws Exception {
        super.handleFind(request, response, crud, elepy, objectEvaluators, clazz);
    }
}
