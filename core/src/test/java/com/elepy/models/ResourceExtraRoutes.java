package com.elepy.models;


import com.elepy.annotations.ElepyConstructor;
import com.elepy.annotations.Inject;
import com.elepy.annotations.Route;
import com.elepy.concepts.Resource;
import com.elepy.dao.jongo.MongoDao;
import spark.Request;
import spark.Response;
import spark.route.HttpMethod;

public class ResourceExtraRoutes {

    private final MongoDao<Resource> resourceCrud;

    @ElepyConstructor
    public ResourceExtraRoutes(
            @Inject(tag = "/resources") MongoDao<Resource> resourceCrud
    ) {
        this.resourceCrud = resourceCrud;
    }

    @Route(path = "/resources-extra", requestMethod = HttpMethod.get)
    public String newRoute(Request request, Response response) {
        response.status(201);
        return "generated";
    }
}
