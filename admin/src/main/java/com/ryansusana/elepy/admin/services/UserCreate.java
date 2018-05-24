package com.ryansusana.elepy.admin.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ryansusana.elepy.admin.ElepyAdminPanel;
import com.ryansusana.elepy.admin.models.User;
import com.ryansusana.elepy.concepts.IntegrityEvaluatorImpl;
import com.ryansusana.elepy.concepts.ObjectEvaluator;
import com.ryansusana.elepy.dao.Crud;
import com.ryansusana.elepy.exceptions.RestErrorMessage;
import com.ryansusana.elepy.routes.Create;
import spark.Request;
import spark.Response;

import java.util.List;
import java.util.Optional;

public class UserCreate implements Create<User> {
    @Override
    public Optional<User> create(Request request, Response response, Crud<User> dao, Class<? extends User> clazz, ObjectMapper objectMapper, List<ObjectEvaluator<User>> objectEvaluators) throws Exception {
        String body = request.body();
        User user = objectMapper.readValue(body, clazz);
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
        return Optional.ofNullable(user);
    }
}
