package com.elepy.routes;

import com.elepy.concepts.ObjectEvaluator;
import com.elepy.dao.Crud;
import com.elepy.dao.QuerySetup;
import com.elepy.dao.SortOption;
import com.elepy.di.ElepyContext;
import com.elepy.http.HttpContext;
import com.elepy.http.Request;
import com.elepy.http.Response;
import com.elepy.utils.ClassUtils;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

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
        response.result(objectMapper.writeValueAsString(dao.search(new QuerySetup(q, fieldSort, ((fieldDirection != null && fieldDirection.toLowerCase().contains("desc")) ? SortOption.DESCENDING : SortOption.ASCENDING), pageNumber, pageSize))));

    }

    public void findOne(Request request, Response response, Crud<T> dao, ObjectMapper objectMapper, Class<T> clazz) throws JsonProcessingException {
        response.type("application/json");

        Object paramId = ClassUtils.toObjectIdFromString(clazz, request.params("id"));

        final Optional<T> id = dao.getById(paramId);
        if (id.isPresent()) {
            response.status(200);
            response.result(objectMapper.writeValueAsString(id.get()));
        } else {
            response.status(404);
            response.result("");
        }
    }


    @Override
    public void handleFind(HttpContext context, Crud<T> crud, ElepyContext elepy, List<ObjectEvaluator<T>> objectEvaluators, Class<T> clazz) throws Exception {
        if (context.request().params("id") != null && !context.request().params("id").isEmpty()) {
            findOne(context.request(), context.response(), crud, elepy.getObjectMapper(), clazz);
        } else {
            find(context.request(), context.response(), crud, elepy.getObjectMapper());
        }
    }
}
