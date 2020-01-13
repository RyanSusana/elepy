package com.elepy.auth.users;

import com.elepy.auth.Permissions;
import com.elepy.auth.User;
import com.elepy.exceptions.ElepyException;
import com.elepy.exceptions.Message;
import com.elepy.handlers.ActionHandler;
import com.elepy.http.HttpContext;
import com.elepy.models.ModelContext;

public class UserDelete implements ActionHandler<User> {
    @Override
    public void handle(HttpContext context, ModelContext<User> modelContext) {
        final var id = context.recordId();
        final User toDelete = modelContext.getCrud().getById(id).orElseThrow(() -> new ElepyException("No user with this ID is found.", 404));
        final User loggedInUser = context.request().loggedInUserOrThrow();

        if (loggedInUser.equals(toDelete)) {
            throw new ElepyException("You can't delete yourself!", 403);
        }
        if (toDelete.getPermissions().contains(Permissions.SUPER_USER)) {
            throw new ElepyException(String.format("You can't delete users with the permission '%s'", Permissions.SUPER_USER), 403);
        }
        modelContext.getCrud().deleteById(toDelete.getId());

        context.response().result(Message.of("Successfully deleted user", 200));
    }
}
