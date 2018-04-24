package com.ryansusana.elepy.admin;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ryansusana.elepy.concepts.ObjectEvaluator;
import com.ryansusana.elepy.concepts.ObjectUpdateEvaluatorImpl;
import com.ryansusana.elepy.dao.Crud;
import com.ryansusana.elepy.routes.Update;
import spark.Request;
import spark.Response;

import java.util.List;
import java.util.Optional;

public class UserUpdate implements Update<User> {
    @Override
    public boolean update(Request request, Response response, Crud<User> dao, Class<? extends User> clazz, ObjectMapper objectMapper, List<ObjectEvaluator<User>> objectEvaluators) throws Exception {
        String body = request.body();

        User updated = objectMapper.readValue(body, clazz);


        Optional<User> before = dao.getById(dao.getId(updated));

        if (!before.isPresent()) {
            response.status(404);
            response.body("No object with this id");
            return false;
        }
        ObjectUpdateEvaluatorImpl<User> updateEvaluator = new ObjectUpdateEvaluatorImpl<>();

        updateEvaluator.evaluate(before.get(), updated);

        for (ObjectEvaluator<User> objectEvaluator : objectEvaluators) {
            if (updated != null) {
                objectEvaluator.evaluate(updated);
            }
        }

        assert updated != null;
        if (!updated.getPassword().equals(before.get().getPassword())) {
            updated = updated.hashWord();
        }


        dao.update(updated);
        response.status(200);
        response.body("The item is updated");
        return true;
    }
}
