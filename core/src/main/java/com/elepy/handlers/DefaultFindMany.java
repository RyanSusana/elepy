package com.elepy.handlers;

import com.elepy.dao.Crud;
import com.elepy.http.HttpContext;
import com.elepy.http.Request;
import com.elepy.http.Response;
import com.elepy.models.ModelContext;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

import static com.elepy.dao.Filters.*;
import static com.elepy.dao.Queries.create;

public class DefaultFindMany<T> implements ActionHandler<T> {


    public List<T> find(Request request, Response response, Crud<T> dao, ModelContext<T> modelContext) {

        response.type("application/json");

        if (request.queryParams("ids") != null) {
            return dao.getByIds(request.recordIds());
        } else {
            String q = Optional.ofNullable(request.queryParams("q")).orElse("");

            String ps = request.queryParams("pageSize");
            String pn = request.queryParams("pageNumber");

            int pageSize = ps == null ? Integer.MAX_VALUE : Integer.parseInt(ps);
            int pageNumber = pn == null ? 1 : Integer.parseInt(pn);

            response.status(200);
            final var sortingSpec = request.sortingForModel(modelContext.getSchema());

            final var or = or(request.filtersForModel(modelContext.getModelType()));
            return dao.find(create(and(search(q), or.getExpressions().isEmpty() ? search("") : or)).purge().sort(sortingSpec).page(pageNumber, pageSize));


        }

    }


    @Override
    public void handle(HttpContext context, ModelContext<T> modelContext) throws Exception {
        Collection<T> result = find(context.request(), context.response(), modelContext.getCrud(), modelContext);

        context.response().json(result);
    }
}
