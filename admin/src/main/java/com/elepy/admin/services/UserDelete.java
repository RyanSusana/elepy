package com.elepy.admin.services;

import com.elepy.admin.ElepyAdminPanel;
import com.elepy.admin.models.User;
import com.elepy.dao.Crud;
import com.elepy.exceptions.RestErrorMessage;
import com.elepy.routes.DeleteHandler;
import com.fasterxml.jackson.databind.ObjectMapper;
import spark.Request;
import spark.Response;

import java.util.Optional;

public class UserDelete implements DeleteHandler<User> {
    @Override
    public boolean delete(Request request, Response response, Crud<User> dao, ObjectMapper objectMapper) {
        final Optional<User> toDelete = dao.getById(request.params("id"));
        User loggedInUser = request.session().attribute(ElepyAdminPanel.ADMIN_USER);
        if (!toDelete.isPresent()) {
            return true;
        }
        if (loggedInUser.getId().equals(toDelete.get().getId())) {
            throw new RestErrorMessage("You can't delete yourself!");
        }
        if (!loggedInUser.getUserType().hasMoreRightsThan(toDelete.get().getUserType())) {
            throw new RestErrorMessage("You can't delete users with an equal or greater rank than you!");
        }
        dao.delete(toDelete.get().getId());
        return true;
    }
}
