package com.elepy.auth.users;

import com.elepy.auth.Permissions;
import com.elepy.auth.User;
import com.elepy.dao.Crud;
import com.elepy.describers.ModelContext;
import com.elepy.exceptions.ElepyException;
import com.elepy.exceptions.Message;
import com.elepy.http.HttpContext;
import com.elepy.routes.DeleteHandler;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.Optional;

public class UserDelete implements DeleteHandler<User> {
    @Override
    public void handleDelete(HttpContext context, Crud<User> crud, ModelContext<User> modelContext, ObjectMapper objectMapper) throws Exception {
        final Optional<User> toDelete = crud.getById(context.modelId());
        User loggedInUser = context.request().loggedInUser();
        if (!toDelete.isPresent()) {
            throw new ElepyException("No user with this ID is found.", 404);
        }
        if (loggedInUser.getId().equals(toDelete.get().getId())) {
            throw new ElepyException("You can't DELETE yourself!");
        }
        if (toDelete.get().getPermissions().contains(Permissions.SUPER_USER)) {
            throw new ElepyException("You can't delete Super Users!");
        }
        crud.deleteById(toDelete.get().getId());

        context.response().result(Message.of("Successfully deleted user", 200));
    }
}
