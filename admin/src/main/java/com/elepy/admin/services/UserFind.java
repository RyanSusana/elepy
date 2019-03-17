package com.elepy.admin.services;

import com.elepy.admin.models.User;
import com.elepy.dao.Crud;
import com.elepy.dao.Page;
import com.elepy.dao.QuerySetup;
import com.elepy.dao.SortOption;
import com.elepy.describers.ModelDescription;
import com.elepy.http.HttpContext;
import com.elepy.http.Request;
import com.elepy.http.Response;
import com.elepy.routes.FindHandler;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.Optional;
import java.util.stream.Collectors;

public class UserFind implements FindHandler<User> {
    public void find(Request request, Response response, Crud<User> dao, ObjectMapper objectMapper) throws JsonProcessingException {

        response.type("application/json");
        String q = request.queryParams("q");

        String fieldSort = request.queryParams("sortBy");

        String fieldDirection = request.queryParams("sortDirection");

        String ps = request.queryParams("pageSize");
        String pn = request.queryParams("pageNumber");

        int pageSize = ps == null ? Integer.MAX_VALUE : Integer.parseInt(ps);
        long pageNumber = pn == null ? 1 : Long.parseLong(pn);


        response.status(200);

        Page<User> desc = dao.search(new QuerySetup(q, fieldSort, ((fieldDirection != null && fieldDirection.toLowerCase().contains("desc")) ? SortOption.DESCENDING : SortOption.ASCENDING), pageNumber, pageSize));

        Page<User> userPage = new Page<>(desc.getCurrentPageNumber(), desc.getLastPageNumber(), desc.getValues().stream().map(user -> user.emptyWord()).collect(Collectors.toList()));
        response.result(objectMapper.writeValueAsString(userPage));

    }

    public void findOne(Request request, Response response, Crud<User> dao, ObjectMapper objectMapper) throws JsonProcessingException {
        response.type("application/json");

        Object paramId = request.modelId();

        final Optional<User> id = dao.getById(paramId);
        if (id.isPresent()) {
            response.status(200);
            response.result(objectMapper.writeValueAsString(id.get().emptyWord()));
        } else {
            response.status(404);
            response.result("");
        }
    }


    @Override
    public void handleFindMany(HttpContext context, Crud<User> crud, ModelDescription<User> modelDescription, ObjectMapper objectMapper) throws Exception {
        find(context.request(), context.response(), crud, objectMapper);
    }

    @Override
    public void handleFindOne(HttpContext context, Crud<User> crud, ModelDescription<User> modelDescription, ObjectMapper objectMapper) throws Exception {
        findOne(context.request(), context.response(), crud, objectMapper);
    }
}
