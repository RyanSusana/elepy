package com.elepy.admin.services;

import com.elepy.admin.ElepyAdminPanel;
import com.elepy.concepts.IntegrityEvaluatorImpl;
import com.elepy.concepts.ObjectEvaluator;
import com.elepy.concepts.ObjectUpdateEvaluatorImpl;
import com.elepy.dao.Crud;
import com.elepy.exceptions.RestErrorMessage;
import com.elepy.routes.Update;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.elepy.admin.models.User;
import spark.Request;
import spark.Response;

import java.util.List;
import java.util.Optional;

public class UserUpdate implements Update<User> {
    @Override
    public Optional<User> update(Request request, Response response, Crud<User> dao, Class<? extends User> clazz, ObjectMapper objectMapper, List<ObjectEvaluator<User>> objectEvaluators) throws Exception {
        String body = request.body();
        User loggedInUser = request.session().attribute(ElepyAdminPanel.ADMIN_USER);
        User updated = objectMapper.readValue(body, clazz);


        Optional<User> before = dao.getById(dao.getId(updated));


        if (!before.isPresent()) {
            response.status(404);
            throw new RestErrorMessage("No object found with this ID");
        }
        if (!loggedInUser.getId().equals(updated.getId())) {
            if (!loggedInUser.getUserType().hasMoreRightsThan(updated.getUserType()) || !loggedInUser.getUserType().hasMoreRightsThan(before.get().getUserType())) {
                throw new RestErrorMessage("You are not allowed to update users with an equal or higher rank than you!");
            }
        } else {
            if (!loggedInUser.getUserType().equals(updated.getUserType())) {
                throw new RestErrorMessage("You can't promote/demote yourself!");

            }
        }

        ObjectUpdateEvaluatorImpl<User> updateEvaluator = new ObjectUpdateEvaluatorImpl<>();

        updateEvaluator.evaluate(before.get(), updated);

        for (ObjectEvaluator<User> objectEvaluator : objectEvaluators) {
            if (updated != null) {
                objectEvaluator.evaluate(updated);
            }
        }
        new IntegrityEvaluatorImpl<User>().evaluate(updated, dao);
        assert updated != null;
        if (!updated.getPassword().equals(before.get().getPassword())) {
            updated = updated.hashWord();
        }


        dao.update(updated);
        response.status(200);
        response.body("The item is updated");
        return Optional.of(updated);
    }
}
