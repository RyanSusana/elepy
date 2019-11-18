package com.elepy;


import com.elepy.annotations.ElepyConstructor;
import com.elepy.annotations.Inject;
import com.elepy.annotations.Route;
import com.elepy.dao.Crud;
import com.elepy.http.HttpMethod;
import com.elepy.http.Request;
import com.elepy.http.Response;

public class ResourceExtraRoutes {

    private Crud<Resource> resourceCrud;

    @ElepyConstructor
    public ResourceExtraRoutes(Crud<Resource> resourceCrud) {
        this.resourceCrud = resourceCrud;
    }

    @Route(path = "/resources-extra", method = HttpMethod.GET)
    public String newRoute(Request request, Response response) {
        response.status(201);
        return "generated";
    }
}
