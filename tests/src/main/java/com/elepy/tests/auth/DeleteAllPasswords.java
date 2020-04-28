package com.elepy.tests.auth;

import com.elepy.exceptions.Message;
import com.elepy.handlers.ActionHandler;
import com.elepy.http.HttpContext;
import com.elepy.models.ModelContext;

import static com.elepy.dao.Filters.any;

public class DeleteAllPasswords implements ActionHandler<Password> {
    @Override
    public void handle(HttpContext context, ModelContext<Password> modelContext) throws Exception {
        modelContext.getCrud().delete(any());
        context.status(200);
        context.result(Message.of("Aha! All of the passwords are gone."));
    }
}
