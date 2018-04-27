package com.ryansusana.elepy.routes;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ryansusana.elepy.concepts.IntegrityEvaluatorImpl;
import com.ryansusana.elepy.concepts.ObjectEvaluator;
import com.ryansusana.elepy.concepts.ObjectUpdateEvaluatorImpl;
import com.ryansusana.elepy.dao.Crud;
import com.ryansusana.elepy.models.RestErrorMessage;
import spark.Request;
import spark.Response;

import java.util.List;
import java.util.Optional;

public class DefaultUpdate<T> implements Update<T> {


    @Override
    public Optional<T> update(Request request, Response response, Crud<T> dao, Class<? extends T> clazz, ObjectMapper objectMapper, List<ObjectEvaluator<T>> objectEvaluators) throws Exception {
        String body = request.body();

        T updated = objectMapper.readValue(body, clazz);

        Optional<T> before = dao.getById(dao.getId(updated));

        if (!before.isPresent()) {
            response.status(404);
            throw new RestErrorMessage("No object found with this ID");
        }

        ObjectUpdateEvaluatorImpl<T> updateEvaluator = new ObjectUpdateEvaluatorImpl<T>();

        updateEvaluator.evaluate(before.get(), updated);

        for (ObjectEvaluator<T> objectEvaluator : objectEvaluators) {
            if (updated != null) {
                objectEvaluator.evaluate(updated);

            }
        }

        new IntegrityEvaluatorImpl<T>().evaluate(updated, dao);
        dao.update(updated);
        response.status(200);
        response.body("The item is updated");
        return Optional.of(updated);
    }
}
