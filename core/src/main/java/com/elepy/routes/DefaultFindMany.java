package com.elepy.routes;

import com.elepy.dao.Crud;
import com.elepy.dao.Page;
import com.elepy.dao.QuerySetup;
import com.elepy.dao.SortOption;
import com.elepy.describers.ModelDescription;
import com.elepy.http.HttpContext;
import com.elepy.http.Request;
import com.elepy.http.Response;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class DefaultFindMany<T> implements FindManyHandler<T> {

    @Override
    public void handleFindMany(HttpContext context, Crud<T> crud, ModelDescription<T> modelDescription, ObjectMapper objectMapper) throws Exception {
        Page<T> page = find(context.request(), context.response(), crud, objectMapper);

        context.response().result(objectMapper.writeValueAsString(page));
    }

    public Page<T> find(Request request, Response response, Crud<T> dao, ObjectMapper objectMapper) throws JsonProcessingException {

        response.type("application/json");
        String q = request.queryParams("q");

        String fieldSort = request.queryParams("sortBy");

        String fieldDirection = request.queryParams("sortDirection");

        String ps = request.queryParams("pageSize");
        String pn = request.queryParams("pageNumber");

        int pageSize = ps == null ? Integer.MAX_VALUE : Integer.parseInt(ps);
        long pageNumber = pn == null ? 1 : Long.parseLong(pn);


        response.status(200);
        return dao.search(new QuerySetup(q, fieldSort, ((fieldDirection != null && fieldDirection.toLowerCase().contains("desc")) ? SortOption.DESCENDING : SortOption.ASCENDING), pageNumber, pageSize));
    }


}
