package com.elepy.models;

import com.elepy.annotations.Inject;
import com.elepy.annotations.Route;
import com.elepy.dao.Crud;
import com.elepy.dao.ResourceDao;
import com.elepy.http.HttpMethod;
import com.elepy.http.Request;
import com.elepy.http.Response;
import com.elepy.routes.DefaultService;

import java.util.Optional;

public class ResourceService extends DefaultService<Resource> {

    @Inject(tag = "/resources", classType = Crud.class)
    private ResourceDao crud;

    @Route(path = "/resources/:id/extra", requestMethod = HttpMethod.GET)
    public void extraRoute(Request request, Response response) {

        Optional<Resource> id = crud.getById(request.modelId());

        if (id.isPresent()) {
            response.status(200);
            response.result(id.get().getTextField());
        } else {
            response.status(400);
            response.result("I am not here");
        }
    }
}
