package com.elepy.auth.users;

import com.elepy.annotations.Inject;
import com.elepy.auth.Permissions;
import com.elepy.auth.Policy;
import com.elepy.auth.User;
import com.elepy.exceptions.ElepyException;
import com.elepy.exceptions.Message;
import com.elepy.handlers.ActionHandler;
import com.elepy.handlers.Context;
import com.elepy.http.HttpContext;
import com.elepy.models.ModelContext;

public class UserDelete implements ActionHandler<User> {

    @Inject
    private Policy policy;

    @Override
    public void handle(Context<User> ctx) {
        final var context = ctx.http();
        final var modelContext = ctx.model();
        final var id = context.recordId();
        final User toDelete = ctx.crud().getById(id).orElseThrow(() -> new ElepyException("No user with this ID is found.", 404));
        final User loggedInUser = context.request().loggedInUserOrThrow();

        if (loggedInUser.equals(toDelete)) {
            throw new ElepyException("You can't delete yourself!", 403);
        }
        if (policy.userHasRole(toDelete, "owner")) {
            throw new ElepyException(String.format("You can't delete users with the permission '%s'", Permissions.SUPER_USER), 403);
        }
        ctx.crud().deleteById(toDelete.getId());

        context.response().result(Message.of("Successfully deleted user", 200));
    }
}
