package com.elepy.tests.auth;

import com.elepy.exceptions.Message;
import com.elepy.handlers.ActionHandler;
import com.elepy.handlers.Context;
import com.elepy.http.HttpContext;
import com.elepy.models.ModelContext;

import static com.elepy.dao.Filters.any;

public class DeleteAllPasswords implements ActionHandler<Password> {
    @Override
    public void handle(Context<Password> ctx) throws Exception {
        ctx.crud().delete(any());
        ctx.http().status(200);
        ctx.http().result(Message.of("Aha! All of the passwords are gone."));
    }
}
