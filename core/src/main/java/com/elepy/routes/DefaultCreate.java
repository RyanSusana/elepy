package com.elepy.routes;

import com.elepy.concepts.AtomicIntegrityEvaluator;
import com.elepy.concepts.IntegrityEvaluatorImpl;
import com.elepy.concepts.ObjectEvaluator;
import com.elepy.dao.Crud;
import com.elepy.utils.ClassUtils;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import spark.Request;
import spark.Response;

import java.util.List;
import java.util.Optional;

public class DefaultCreate<T> implements CreateHandler<T> {

    @Override
    public boolean create(Request request, Response response, Crud<T> dao, ObjectMapper objectMapper, List<ObjectEvaluator<T>> objectEvaluators) throws Exception {
        String body = request.body();

        try {
            JavaType type = objectMapper.getTypeFactory().constructCollectionType(List.class, dao.getType());

            final List<T> ts = objectMapper.readValue(body, type);
            return multipleCreate(ts, dao, objectEvaluators).isPresent();
        } catch (JsonMappingException e) {

            T item = objectMapper.readValue(body, dao.getType());
            return defaultCreate(item, dao, objectMapper, objectEvaluators).isPresent();
        }
    }

    protected Optional<T> defaultCreate(T product, Crud<T> dao, ObjectMapper objectMapper, List<ObjectEvaluator<T>> objectEvaluators) throws Exception {
        for (ObjectEvaluator<T> objectEvaluator : objectEvaluators) {
            objectEvaluator.evaluate(product);
        }
        new IntegrityEvaluatorImpl<T>().evaluate(product, dao);
        dao.create(product);
        return Optional.ofNullable(product);
    }

    protected Optional<Iterable<T>> multipleCreate(Iterable<T> items, Crud<T> dao, List<ObjectEvaluator<T>> objectEvaluators) throws Exception {
        if (ClassUtils.hasIntegrityRules(dao.getType())) {
            new AtomicIntegrityEvaluator<T>().evaluate(Lists.newArrayList(Iterables.toArray(items, dao.getType())));
        }

        for (T item : items) {
            for (ObjectEvaluator<T> objectEvaluator : objectEvaluators) {
                objectEvaluator.evaluate(item);
            }
            new IntegrityEvaluatorImpl<T>().evaluate(item, dao);
        }
        dao.create(items);
        return Optional.of(items);

    }
}
