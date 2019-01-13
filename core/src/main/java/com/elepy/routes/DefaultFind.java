package com.elepy.routes;

import com.elepy.concepts.ObjectEvaluator;
import com.elepy.dao.Crud;
import com.elepy.dao.QuerySetup;
import com.elepy.dao.SortOption;
import com.elepy.di.ElepyContext;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import spark.Request;
import spark.Response;

import java.util.List;
import java.util.Optional;

public class DefaultFind<T> implements FindHandler<T> {


    public void find(Request request, Response response, Crud<T> dao, ObjectMapper objectMapper) throws JsonProcessingException {

        response.type("application/json");
        String q = request.queryParams("q");

        String fieldSort = request.queryParams("sortBy");

        String fieldDirection = request.queryParams("sortDirection");

        String ps = request.queryParams("pageSize");
        String pn = request.queryParams("pageNumber");

        int pageSize = ps == null ? Integer.MAX_VALUE : Integer.parseInt(ps);
        long pageNumber = pn == null ? 1 : Long.parseLong(pn);


        response.status(200);
        response.body(objectMapper.writeValueAsString(dao.search(new QuerySetup(q, fieldSort, ((fieldDirection != null && fieldDirection.toLowerCase().contains("desc")) ? SortOption.DESCENDING : SortOption.ASCENDING), pageNumber, pageSize))));

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
    public void handleFind(Request request, Response response, Crud<T> crud, ElepyContext elepy, List<ObjectEvaluator<T>> objectEvaluators, Class<T> clazz) throws JsonProcessingException {
        if (request.params("id") != null && !request.params("id").isEmpty()) {
            findOne(request, response, crud, elepy.getObjectMapper());
        } else {
            find(request, response, crud, elepy.getObjectMapper());
        }
    }
}
