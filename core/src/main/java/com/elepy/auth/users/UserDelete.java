package com.elepy.auth.users;

import jakarta.inject.Inject;
import com.elepy.auth.Policy;
import com.elepy.auth.User;
import com.elepy.exceptions.ElepyException;
import com.elepy.exceptions.Message;
import com.elepy.handlers.ActionHandler;
import com.elepy.handlers.HandlerContext;

public class UserDelete implements ActionHandler<User> {

    @Inject
    private Policy policy;

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
        if (policy.userHasRole(toDelete, "owner")) {
            throw ElepyException.translated(403, "{elepy.models.users.exceptions.owner}");
        }
        ctx.crud().deleteById(toDelete.getId());

        context.response().result(Message.of("Successfully deleted user", 200));
    }
}
