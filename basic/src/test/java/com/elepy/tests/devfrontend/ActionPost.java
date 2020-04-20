package com.elepy.tests.devfrontend;

import com.elepy.handlers.ActionHandler;
import com.elepy.http.HttpContext;
import com.elepy.models.ModelContext;

public class ActionPost implements ActionHandler<Post> {
    @Override
    public void handle(HttpContext context, ModelContext<Post> modelContext) throws Exception {
        final var postActionInput = context.request().inputAs(PostActionInput.class);


        System.out.println(context.body());
    }
}
