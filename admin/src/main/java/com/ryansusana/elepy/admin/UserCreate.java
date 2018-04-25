package com.ryansusana.elepy.admin;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ryansusana.elepy.concepts.IntegrityEvaluatorImpl;
import com.ryansusana.elepy.concepts.ObjectEvaluator;
import com.ryansusana.elepy.dao.Crud;
import com.ryansusana.elepy.routes.Create;
import spark.Request;
import spark.Response;

import java.util.List;
import java.util.Optional;

public class UserCreate implements Create<User> {
    @Override
    public Optional<User> create(Request request, Response response, Crud<User> dao, Class<? extends User> clazz, ObjectMapper objectMapper, List<ObjectEvaluator<User>> objectEvaluators) throws Exception {
        String body = request.body();
        User product = objectMapper.readValue(body, clazz);

        for (ObjectEvaluator<User> objectEvaluator : objectEvaluators) {
            objectEvaluator.evaluate(product);
        }
        new IntegrityEvaluatorImpl<User>().evaluate(product, dao);
        product = product.hashWord();
        dao.create(product);
        response.body(request.body());
        return Optional.ofNullable(product);
    }
}
