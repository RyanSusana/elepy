package com.elepy.admin.services;

import com.elepy.admin.ElepyAdminPanel;
import com.elepy.admin.models.User;
import com.elepy.dao.Crud;
import com.elepy.describers.ModelDescription;
import com.elepy.evaluators.DefaultIntegrityEvaluator;
import com.elepy.evaluators.DefaultObjectUpdateEvaluator;
import com.elepy.evaluators.ObjectEvaluator;
import com.elepy.exceptions.ElepyException;
import com.elepy.exceptions.Message;
import com.elepy.http.HttpContext;
import com.elepy.routes.UpdateHandler;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.Optional;

public class UserUpdate implements UpdateHandler<User> {

    @Override
    public void handleUpdatePut(HttpContext context, Crud<User> crud, ModelDescription<User> modelDescription, ObjectMapper objectMapper) throws Exception {

        String body = context.request().body();
        User loggedInUser = context.request().attribute(ElepyAdminPanel.ADMIN_USER);
        User updated = objectMapper.readValue(body, modelDescription.getModelType());

        Optional<User> before = crud.getById(crud.getId(updated));

        if (!before.isPresent()) {
            context.response().status(404);
            throw new ElepyException("No object found with this ID");
        }
        if (!loggedInUser.getId().equals(updated.getId())) {
            if (!loggedInUser.getUserType().hasMoreRightsThan(updated.getUserType()) || !loggedInUser.getUserType().hasMoreRightsThan(before.get().getUserType())) {
                throw new ElepyException("You are not allowed to update users with an equal or higher rank than you!");
            }
        } else {
            if (!loggedInUser.getUserType().equals(updated.getUserType())) {
                throw new ElepyException("You can't promote/demote yourself!");

            }
        }

        DefaultObjectUpdateEvaluator<User> updateEvaluator = new DefaultObjectUpdateEvaluator<>();

        updateEvaluator.evaluate(before.get(), updated);

        for (ObjectEvaluator<User> objectEvaluator : modelDescription.getObjectEvaluators()) {
            objectEvaluator.evaluate(updated, User.class);
        }
        new DefaultIntegrityEvaluator<User>().evaluate(updated, crud);

        if (updated.getPassword().isEmpty()) {
            updated.setPassword(before.get().getPassword());
        }
        if (!updated.getPassword().equals(before.get().getPassword())) {
            updated = updated.hashWord();
        }


        crud.update(updated);
        context.response().status(200);
        context.response().result(Message.of("The user has been updated", 200));
    }

    @Override
    public void handleUpdatePatch(HttpContext context, Crud<User> crud, ModelDescription<User> modelDescription, ObjectMapper objectMapper) throws Exception {
        handleUpdatePut(context, crud, modelDescription, objectMapper);
    }
}
