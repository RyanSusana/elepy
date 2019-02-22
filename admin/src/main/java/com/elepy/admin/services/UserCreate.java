package com.elepy.admin.services;

import com.elepy.admin.ElepyAdminPanel;
import com.elepy.admin.models.User;
import com.elepy.concepts.IntegrityEvaluatorImpl;
import com.elepy.concepts.ObjectEvaluator;
import com.elepy.dao.Crud;
import com.elepy.describers.ModelDescription;
import com.elepy.exceptions.ElepyException;
import com.elepy.http.HttpContext;
import com.elepy.routes.CreateHandler;
import com.fasterxml.jackson.databind.ObjectMapper;

public class UserCreate implements CreateHandler<User> {
    @Override
    public void handleCreate(HttpContext context, Crud<User> crud, ModelDescription<User> modelDescription, ObjectMapper objectMapper) throws Exception {
        String body = context.request().body();
        User user = objectMapper.readValue(body, crud.getType());
        User loggedInUser = context.request().session().attribute(ElepyAdminPanel.ADMIN_USER);


        for (ObjectEvaluator<User> objectEvaluator : modelDescription.getObjectEvaluators()) {
            objectEvaluator.evaluate(user, User.class);
        }
        new IntegrityEvaluatorImpl<User>().evaluate(user, crud, true);

        if (!loggedInUser.getUserType().hasMoreRightsThan(user.getUserType())) {

            throw new ElepyException("You are not allowed to singleCreate users with an equal higher rank than you!");
        }
        user = user.hashWord();
        crud.create(user);
        context.response().result(context.request().body());
    }
}
