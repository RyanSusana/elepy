package com.elepy.auth.users;

import com.elepy.auth.Permissions;
import com.elepy.auth.User;
import com.elepy.dao.Crud;
import com.elepy.exceptions.ElepyException;
import com.elepy.exceptions.Message;
import com.elepy.handlers.DeleteHandler;
import com.elepy.http.HttpContext;
import com.elepy.models.ModelContext;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.Serializable;

public class UserDelete implements DeleteHandler<User> {
    @Override
    public void handleDelete(HttpContext context, Crud<User> crud, ModelContext<User> modelContext, ObjectMapper objectMapper) throws Exception {
        final var id = context.recordId();
        final User toDelete = crud.getById(id).orElseThrow(() -> new ElepyException("No user with this ID is found.", 404));
        final User loggedInUser = context.request().loggedInUserOrThrow();

        if (loggedInUser.equals(toDelete)) {
            throw new ElepyException("You can't delete yourself!", 403);
        }
        if (toDelete.getPermissions().contains(Permissions.SUPER_USER)) {
            throw new ElepyException(String.format("You can't delete users with the permission '%s'", Permissions.SUPER_USER), 403);
        }
        crud.deleteById(toDelete.getId());

        context.response().result(Message.of("Successfully deleted user", 200));
    }
}
