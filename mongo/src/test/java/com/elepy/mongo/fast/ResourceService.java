package com.elepy.mongo.fast;

import com.elepy.annotations.Inject;
import com.elepy.annotations.Route;
import com.elepy.dao.Crud;
import com.elepy.handlers.DefaultService;
import com.elepy.http.HttpMethod;
import com.elepy.http.Request;
import com.elepy.http.Response;

import java.util.Optional;

public class ResourceService extends DefaultService<Resource> {

    @Inject
    private Crud<Resource> crud;

    @Route(path = "/resources/:id/extra", requiredPermissions = {}, method = HttpMethod.GET)
    public void extraRoute(Request request, Response response) {

        Optional<Resource> id = crud.getById(request.recordId());

        if (id.isPresent()) {
            response.status(200);
            response.result(id.get().getTextField());
        } else {
            response.status(400);
            response.result("I am not here");
        }
    }
}
