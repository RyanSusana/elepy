package com.elepy.tests.auth;

import com.elepy.exceptions.Message;
import com.elepy.handlers.ActionHandler;
import com.elepy.handlers.HandlerContext;

import static com.elepy.query.Filters.any;

public class DeleteAllPasswords implements ActionHandler<Password> {
    @Override
    public void handle(HandlerContext<Password> ctx) throws Exception {
        ctx.crud().delete(any());
        ctx.http().status(200);
        ctx.http().result(Message.of("Aha! All of the passwords are gone."));
    }
}
