package com.elepy.admin.services;

import com.elepy.admin.ElepyAdminPanel;
import com.elepy.admin.models.User;
import com.elepy.concepts.IntegrityEvaluatorImpl;
import com.elepy.concepts.ObjectEvaluator;
import com.elepy.dao.Crud;
import com.elepy.di.ElepyContext;
import com.elepy.exceptions.ElepyException;
import com.elepy.http.HttpContext;
import com.elepy.routes.CreateHandler;

import java.util.List;

public class UserCreate implements CreateHandler<User> {
    @Override
    public void handleCreate(HttpContext context, Crud<User> crud, ElepyContext elepy, List<ObjectEvaluator<User>> objectEvaluators, Class<User> clazz) throws Exception {
        String body = context.request().body();
        User user = elepy.getObjectMapper().readValue(body, crud.getType());
        User loggedInUser = context.request().session().attribute(ElepyAdminPanel.ADMIN_USER);


        for (ObjectEvaluator<User> objectEvaluator : objectEvaluators) {
            objectEvaluator.evaluate(user, User.class);
        }
        new IntegrityEvaluatorImpl<User>().evaluate(user, crud);

        if (!loggedInUser.getUserType().hasMoreRightsThan(user.getUserType())) {

            throw new ElepyException("You are not allowed to create users with an equal higher rank than you!");
        }
        user = user.hashWord();
        crud.create(user);
        context.response().result(context.request().body());
    }
}
