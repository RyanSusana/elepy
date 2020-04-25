package com.elepy.tests.devfrontend;

import com.elepy.dao.Filters;
import com.elepy.dao.Queries;
import com.elepy.handlers.ActionHandler;
import com.elepy.http.HttpContext;
import com.elepy.models.ModelContext;

public class ActionPost implements ActionHandler<Post> {
    @Override
    public void handle(HttpContext context, ModelContext<Post> modelContext) throws Exception {
        final var postActionInput = context.request().inputAsString();


        Queries.parse("title=someTitle");


        Queries.create(Filters.eq("title", "someTitle"));
    }
}
