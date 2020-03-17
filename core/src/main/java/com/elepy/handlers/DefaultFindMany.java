package com.elepy.handlers;

import com.elepy.dao.Crud;
import com.elepy.dao.Page;
import com.elepy.dao.PageSettings;
import com.elepy.dao.Query;
import com.elepy.http.HttpContext;
import com.elepy.http.Request;
import com.elepy.http.Response;
import com.elepy.models.ModelContext;

public class DefaultFindMany<T> implements ActionHandler<T> {


    public Page<T> find(Request request, Response response, Crud<T> dao, ModelContext<T> modelContext) {

        response.type("application/json");

        if (request.queryParams("ids") != null) {
            return new Page<>(1, 1, dao.getByIds(request.recordIds()));
        } else {
            String q = request.queryParams("q");

            String ps = request.queryParams("pageSize");
            String pn = request.queryParams("pageNumber");

            int pageSize = ps == null ? Integer.MAX_VALUE : Integer.parseInt(ps);
            int pageNumber = pn == null ? 1 : Integer.parseInt(pn);

            response.status(200);

            return dao.search(new Query(q, request.filtersForModel(modelContext.getModelType())), new PageSettings(pageNumber, pageSize, request.sortingForModel(modelContext.getSchema())));

        }

    }


    @Override
    public void handle(HttpContext context, ModelContext<T> modelContext) throws Exception {
        Page<T> page = find(context.request(), context.response(), modelContext.getCrud(), modelContext);

        context.response().json(page);
    }
}
