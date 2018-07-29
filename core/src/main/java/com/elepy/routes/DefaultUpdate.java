package com.elepy.routes;

import com.elepy.Elepy;
import com.elepy.concepts.IntegrityEvaluatorImpl;
import com.elepy.concepts.ObjectEvaluator;
import com.elepy.concepts.ObjectUpdateEvaluatorImpl;
import com.elepy.dao.Crud;
import com.elepy.exceptions.RestErrorMessage;
import com.fasterxml.jackson.databind.ObjectMapper;
import spark.Request;
import spark.Response;

import java.util.List;
import java.util.Optional;

public class DefaultUpdate<T> implements RouteHandler<T> {

    private T before;

    public T before() {
        if (before == null) {
            throw new IllegalStateException("Before not yet set!");
        }
        return before;
    }

    public T update(Request request, Response response, Crud<T> dao, Elepy elepy, List<ObjectEvaluator<T>> objectEvaluators, Class<T> clazz) throws Exception{
        String body = request.body();

        T updated = elepy.getObjectMapper().readValue(body, clazz);

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
                objectEvaluator.evaluate(updated, clazz);

            }
        }

        new IntegrityEvaluatorImpl<T>().evaluate(updated, dao);
        dao.update(updated);
        response.status(200);
        response.body("OK");
        return updated;
    }
    @Override
    public void handle(Request request, Response response, Crud<T> dao, Elepy elepy, List<ObjectEvaluator<T>> objectEvaluators, Class<T> clazz) throws Exception {
        this.update(request, response, dao, elepy, objectEvaluators, clazz);
    }
}
