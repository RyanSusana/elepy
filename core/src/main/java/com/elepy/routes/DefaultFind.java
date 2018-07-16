package com.elepy.routes;

import com.elepy.Elepy;
import com.elepy.concepts.ObjectEvaluator;
import com.elepy.dao.*;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import spark.Request;
import spark.Response;

import java.util.List;
import java.util.Optional;

public class DefaultFind<T> implements RouteHandler<T> {



    public void find(Request request, Response response, Crud<T> dao, ObjectMapper objectMapper) throws JsonProcessingException {

        response.type("application/json");
        String q = request.queryParams("q");

        String fieldSort = request.queryParams("sortBy");

        String fieldDirection = request.queryParams("sortDirection");

        String ps = request.queryParams("pageSize");
        String pn = request.queryParams("pageNumber");

        int pageSize = ps == null ? Integer.MAX_VALUE : Integer.parseInt(ps);
        int pageNumber = pn == null ? 1 : Integer.parseInt(pn);

        PageSetup pageSetup = new PageSetup(pageSize, pageNumber);

        response.status(200);
        if ((q != null && !q.trim().isEmpty()) || fieldSort != null || fieldDirection != null) {
            response.body( objectMapper.writeValueAsString(dao.search(new SearchSetup(q, fieldSort, (fieldDirection != null && fieldDirection.toLowerCase().contains("desc")) ? SortOption.DESCENDING : SortOption.ASCENDING), pageSetup)));
        }else{

            response.body(objectMapper.writeValueAsString(dao.get(pageSetup)));

        }
    }

    public void findOne(Request request, Response response, Crud<T> dao, ObjectMapper objectMapper) throws JsonProcessingException {
        response.type("application/json");


        final Optional<T> id = dao.getById(request.params("id"));
        if (id.isPresent()) {
            response.status(200);
            response.body(objectMapper.writeValueAsString(id.get()));
        } else {
            response.status(404);
            response.body("");
        }
    }


    @Override
    public void handle(Request request, Response response, Crud<T> crud, Elepy elepy, List<ObjectEvaluator<T>> objectEvaluators, Class<T> clazz) throws JsonProcessingException {
        if (request.params("id") != null) {
            findOne(request, response, crud, elepy.getObjectMapper());
        } else {
            find(request, response, crud, elepy.getObjectMapper());
        }
    }
}
