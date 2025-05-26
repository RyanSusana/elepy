package com.elepy.auth.users;

import com.elepy.exceptions.ElepyException;
import com.elepy.exceptions.Message;
import com.elepy.handlers.ActionHandler;
import com.elepy.handlers.HandlerContext;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

@ApplicationScoped
public class UserDelete implements ActionHandler<User> {

    @Override
    public void handle(HandlerContext<User> ctx) {
        final var context = ctx.http();
        final var modelContext = ctx.model();
        final var id = context.recordId();
        final User toDelete = ctx.crud().getById(id).orElseThrow(() -> ElepyException.notFound("User"));
        final User loggedInUser = context.request().loggedInUserOrThrow();

        if (loggedInUser.equals(toDelete)) {
            throw ElepyException.translated(403, "{elepy.models.users.exceptions.cantDeleteSelf}");
        }
        ctx.crud().deleteById(toDelete.getId());

        context.response().result(Message.of("Successfully deleted user", 200));
    }
}
