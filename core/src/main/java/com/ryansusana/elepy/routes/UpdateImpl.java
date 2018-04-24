package com.ryansusana.elepy.routes;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ryansusana.elepy.concepts.ObjectEvaluator;
import com.ryansusana.elepy.concepts.ObjectUpdateEvaluator;
import com.ryansusana.elepy.concepts.ObjectUpdateEvaluatorImpl;
import com.ryansusana.elepy.dao.Crud;
import spark.Request;
import spark.Response;

import java.util.List;
import java.util.Optional;

public class UpdateImpl<T> implements Update<T> {


    @Override
    public boolean update(Request request, Response response, Crud<T> dao, Class<? extends T> clazz, ObjectMapper objectMapper, List<ObjectEvaluator<T>> objectEvaluators) throws Exception {
        String body = request.body();

        T updated = objectMapper.readValue(body, clazz);

        Optional<T> before = dao.getById(dao.getId(updated));

        if (!before.isPresent()) {
            response.status(404);
            response.body("No object with this id");
            return false;
        }

        ObjectUpdateEvaluator<T> updateEvaluator = new ObjectUpdateEvaluatorImpl();

        updateEvaluator.evaluate(before.get(), updated);

        for (ObjectEvaluator<T> objectEvaluator : objectEvaluators) {
            if(updated!=null) {
                objectEvaluator.evaluate(updated);

            }
        }


        dao.update(updated);
        response.status(200);
        response.body("The item is updated");
        return true;
    }
}
