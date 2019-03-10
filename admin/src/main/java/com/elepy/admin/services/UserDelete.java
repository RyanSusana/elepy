package com.elepy.admin.services;

import com.elepy.admin.ElepyAdminPanel;
import com.elepy.admin.models.User;
import com.elepy.dao.Crud;
import com.elepy.describers.ModelDescription;
import com.elepy.exceptions.ElepyException;
import com.elepy.http.HttpContext;
import com.elepy.routes.DeleteHandler;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.Optional;

public class UserDelete implements DeleteHandler<User> {
    @Override
    public void handleDelete(HttpContext context, Crud<User> crud, ModelDescription<User> modelDescription, ObjectMapper objectMapper) throws Exception {
        final Optional<User> toDelete = crud.getById(context.modelId());
        User loggedInUser = context.request().session().attribute(ElepyAdminPanel.ADMIN_USER);
        if (!toDelete.isPresent()) {
            throw new ElepyException("No user with this ID is found.", 404);
        }
        if (loggedInUser.getId().equals(toDelete.get().getId())) {
            throw new ElepyException("You can't DELETE yourself!");
        }
        if (!loggedInUser.getUserType().hasMoreRightsThan(toDelete.get().getUserType())) {
            throw new ElepyException("You can't DELETE users with an equal or greater rank than you!");
        }
        crud.delete(toDelete.get().getId());
    }
}
