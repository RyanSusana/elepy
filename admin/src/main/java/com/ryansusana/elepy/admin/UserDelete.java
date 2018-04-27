package com.ryansusana.elepy.admin;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ryansusana.elepy.dao.Crud;
import com.ryansusana.elepy.models.RestErrorMessage;
import com.ryansusana.elepy.routes.Delete;
import spark.Request;
import spark.Response;

import java.util.Optional;

public class UserDelete implements Delete<User> {
    @Override
    public Optional<User> delete(Request request, Response response, Crud<User> dao, ObjectMapper objectMapper) {
        final Optional<User> toDelete = dao.getById(request.params("id"));
        User loggedInUser = request.session().attribute(ElepyAdminPanel.ADMIN_USER);
        if (!toDelete.isPresent()) {
            return toDelete;
        }
        if (loggedInUser.getId().equals(toDelete.get().getId())) {
            throw new RestErrorMessage("You can't delete yourself!");
        }
        if (!loggedInUser.getUserType().hasMoreRightsThan(toDelete.get().getUserType())) {
            throw new RestErrorMessage("You can't delete users with an equal or greater rank than you!");
        }
        dao.delete(toDelete.get().getId());
        return toDelete;
    }
}
