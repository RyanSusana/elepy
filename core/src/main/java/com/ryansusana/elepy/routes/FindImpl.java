package com.ryansusana.elepy.routes;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ryansusana.elepy.dao.Crud;
import com.ryansusana.elepy.dao.SearchSetup;
import com.ryansusana.elepy.dao.SortOption;
import spark.Request;
import spark.Response;

import java.util.List;

public class FindImpl<T> implements Find<T> {


    @Override
    public List<T> find(Request request, Response response, Crud<T> dao, ObjectMapper objectMapper) {

        response.type("application/json");
        //evaluateObject(restModel, dao.getById(request.params("id")));
        String q = request.queryParams("q");

        String fieldSort = request.queryParams("sortBy");

        String fieldDirection = request.queryParams(
                "sortDirection"
        );

        if (q != null || fieldSort != null || fieldDirection != null) {

            return dao.search(new SearchSetup(q, fieldSort, (fieldDirection != null && fieldDirection.toLowerCase().contains("desc")) ? SortOption.DESCENDING : SortOption.ASCENDING));
        }
        return dao.getAll();
    }
}
