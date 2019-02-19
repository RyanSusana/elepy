package com.elepy.admin.services;

import com.elepy.admin.ElepyAdminPanel;
import com.elepy.admin.models.User;
import com.elepy.concepts.ObjectEvaluator;
import com.elepy.dao.Crud;
import com.elepy.di.ElepyContext;
import com.elepy.exceptions.ElepyException;
import com.elepy.http.HttpContext;
import com.elepy.routes.DeleteHandler;

import java.util.List;
import java.util.Optional;

public class UserDelete implements DeleteHandler<User> {
    @Override
    public void handleDelete(HttpContext context, Crud<User> crud, ElepyContext elepy, List<ObjectEvaluator<User>> objectEvaluators, Class<User> clazz) throws Exception {
        final Optional<User> toDelete = crud.getById(context.request().params("id"));
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
