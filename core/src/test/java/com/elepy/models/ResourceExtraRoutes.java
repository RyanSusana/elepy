package com.elepy.models;


import com.elepy.annotations.ElepyConstructor;
import com.elepy.annotations.Inject;
import com.elepy.annotations.Route;
import com.elepy.dao.ResourceDao;
import spark.Request;
import spark.Response;
import spark.route.HttpMethod;

public class ResourceExtraRoutes {

    private ResourceDao resourceCrud;

    @ElepyConstructor
    public ResourceExtraRoutes(@Inject ResourceDao resourceCrud) {
        this.resourceCrud = resourceCrud;
    }

    @Route(path = "/resources-extra", requestMethod = HttpMethod.get)
    public String newRoute(Request request, Response response) {
        response.status(201);
        return "generated";
    }
}
