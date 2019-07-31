package com.elepy.auth.users;

import com.elepy.auth.Permissions;
import com.elepy.auth.User;
import com.elepy.dao.Crud;
import com.elepy.exceptions.ElepyException;
import com.elepy.exceptions.Message;
import com.elepy.http.HttpContext;
import com.elepy.models.ModelContext;
import com.elepy.routes.DeleteHandler;
import com.fasterxml.jackson.databind.ObjectMapper;

public class UserDelete implements DeleteHandler<User> {
    @Override
    public void handleDelete(HttpContext context, Crud<User> crud, ModelContext<User> modelContext, ObjectMapper objectMapper) throws Exception {
        final User toDelete = crud.getById(context.modelId()).orElseThrow(() -> new ElepyException("No user with this ID is found.", 404));
        final User loggedInUser = context.request().loggedInUserOrThrow();

        if (loggedInUser.getId().equals(toDelete.getId())) {
            throw new ElepyException("You can't delete yourself!", 403);
        }
        if (toDelete.getPermissions().contains(Permissions.SUPER_USER)) {
            throw new ElepyException(String.format("You can't delete users with the permission '%s'", Permissions.SUPER_USER), 403);
        }
        crud.deleteById(toDelete.getId());

        context.response().result(Message.of("Successfully deleted user", 200));
    }
}
