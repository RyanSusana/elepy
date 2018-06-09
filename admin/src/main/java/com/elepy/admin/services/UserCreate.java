package com.elepy.admin.services;

import com.elepy.admin.ElepyAdminPanel;
import com.elepy.admin.models.User;
import com.elepy.concepts.IntegrityEvaluatorImpl;
import com.elepy.concepts.ObjectEvaluator;
import com.elepy.dao.Crud;
import com.elepy.exceptions.RestErrorMessage;
import com.elepy.routes.Create;
import com.fasterxml.jackson.databind.ObjectMapper;
import spark.Request;
import spark.Response;

import java.util.List;

public class UserCreate implements Create<User> {
    @Override
    public boolean create(Request request, Response response, Crud<User> dao, ObjectMapper objectMapper, List<ObjectEvaluator<User>> objectEvaluators) throws Exception {
        String body = request.body();
        User user = objectMapper.readValue(body, dao.getType());
        User loggedInUser = request.session().attribute(ElepyAdminPanel.ADMIN_USER);


        for (ObjectEvaluator<User> objectEvaluator : objectEvaluators) {
            objectEvaluator.evaluate(user);
        }
        new IntegrityEvaluatorImpl<User>().evaluate(user, dao);

        if (!loggedInUser.getUserType().hasMoreRightsThan(user.getUserType())) {

            throw new RestErrorMessage("You are not allowed to create users with an equal higher rank than you!");
        }
        user = user.hashWord();
        dao.create(user);
        response.body(request.body());
        return true;
    }
}
