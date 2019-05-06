package com.elepy.routes;

import com.elepy.dao.Crud;
import com.elepy.dao.Page;
import com.elepy.dao.PageSettings;
import com.elepy.dao.Query;
import com.elepy.describers.ModelContext;
import com.elepy.http.HttpContext;
import com.elepy.http.Request;
import com.elepy.http.Response;
import com.fasterxml.jackson.databind.ObjectMapper;

public class DefaultFindMany<T> implements FindManyHandler<T> {

    @Override
    public void handleFindMany(HttpContext context, Crud<T> crud, ModelContext<T> modelContext, ObjectMapper objectMapper) throws Exception {
        Page<T> page = find(context.request(), context.response(), crud, modelContext);

        context.response().result(objectMapper.writeValueAsString(page));
    }

    public Page<T> find(Request request, Response response, Crud<T> dao, ModelContext<T> modelContext) {

        response.type("application/json");
        String q = request.queryParams("q");

        String ps = request.queryParams("pageSize");
        String pn = request.queryParams("pageNumber");

        int pageSize = ps == null ? Integer.MAX_VALUE : Integer.parseInt(ps);
        int pageNumber = pn == null ? 1 : Integer.parseInt(pn);

        response.status(200);

        return dao.search(new Query(q, request.filtersForModel(modelContext.getModelType())), new PageSettings(pageNumber, pageSize, request.sortingForModel(modelContext)));
    }


}
