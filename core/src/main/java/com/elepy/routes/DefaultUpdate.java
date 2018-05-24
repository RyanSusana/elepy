package com.elepy.routes;

import com.elepy.concepts.IntegrityEvaluatorImpl;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.elepy.concepts.ObjectEvaluator;
import com.elepy.concepts.ObjectUpdateEvaluatorImpl;
import com.elepy.dao.Crud;
import com.elepy.exceptions.RestErrorMessage;
import spark.Request;
import spark.Response;

import java.util.List;
import java.util.Optional;

public class DefaultUpdate<T> implements Update<T> {

    private T before;

    @Override
    public Optional<T> update(Request request, Response response, Crud<T> dao, Class<? extends T> clazz, ObjectMapper objectMapper, List<ObjectEvaluator<T>> objectEvaluators) throws Exception {
        String body = request.body();

        T updated = objectMapper.readValue(body, clazz);

        Optional<T> before = dao.getById(dao.getId(updated));

        if (!before.isPresent()) {
            response.status(404);
            throw new RestErrorMessage("No object found with this ID");
        }

        this.before = before.get();
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

    public T before() {
        if (before == null) {
            throw new IllegalStateException("Before not yet set!");
        }
        return before;
    }
}
