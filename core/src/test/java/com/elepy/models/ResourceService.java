package com.elepy.models;

import com.elepy.annotations.Inject;
import com.elepy.annotations.Route;
import com.elepy.concepts.Resource;
import com.elepy.dao.jongo.MongoDao;
import com.elepy.routes.DefaultService;
import spark.Request;
import spark.Response;
import spark.route.HttpMethod;

import java.util.Optional;

public class ResourceService extends DefaultService<Resource> {

    @Inject(tag = "/resources")
    private MongoDao<Resource> crud;

    @Route(path = "/resources/:id/extra", requestMethod = HttpMethod.get)
    public void extraRoute(Request request, Response response) {

        Optional<Resource> id = crud.getById(request.params("id"));

        if (id.isPresent()) {
            response.status(200);
            response.body(id.get().getTextField());
        } else {
            response.status(400);
            response.body("I am not here");
        }
    }
}
