package com.ryansusana.elepy.routes;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ryansusana.elepy.concepts.IntegrityEvaluatorImpl;
import com.ryansusana.elepy.concepts.ObjectEvaluator;
import com.ryansusana.elepy.dao.Crud;
import spark.Request;
import spark.Response;

import java.util.List;
import java.util.Optional;

public class DefaultCreate<T> implements Create<T> {
    @Override
    public Optional<T> create(Request request, Response response, Crud<T> dao, Class<? extends T> clazz, ObjectMapper objectMapper, List<ObjectEvaluator<T>> objectEvaluators) throws Exception {

        return defaultCreate(request, response, dao, clazz, objectMapper, objectEvaluators);

    }

    protected Optional<T> defaultCreate(Request request, Response response, Crud<T> dao, Class<? extends T> clazz, ObjectMapper objectMapper, List<ObjectEvaluator<T>> objectEvaluators) throws Exception {
        String body = request.body();
        T product = objectMapper.readValue(body, clazz);

        return defaultCreate(product, dao, objectMapper, objectEvaluators);
    }

    protected Optional<T> defaultCreate(T product, Crud<T> dao, ObjectMapper objectMapper, List<ObjectEvaluator<T>> objectEvaluators) throws Exception {
        for (ObjectEvaluator<T> objectEvaluator : objectEvaluators) {
            objectEvaluator.evaluate(product);
        }
        new IntegrityEvaluatorImpl<T>().evaluate(product, dao);
        dao.create(product);
        return Optional.ofNullable(product);
    }
}
