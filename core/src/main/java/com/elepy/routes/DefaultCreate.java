package com.elepy.routes;

import com.elepy.concepts.AtomicIntegrityEvaluator;
import com.elepy.concepts.IntegrityEvaluatorImpl;
import com.elepy.concepts.ObjectEvaluator;
import com.elepy.dao.Crud;
import com.elepy.describers.ModelDescription;
import com.elepy.http.HttpContext;
import com.elepy.http.Response;
import com.elepy.utils.ClassUtils;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;

import java.util.Collections;
import java.util.List;

public class DefaultCreate<T> implements CreateHandler<T> {


    public T singleCreate(Response response, T item, Crud<T> dao, ModelDescription<T> modelDescription) throws Exception {
        evaluate(item, modelDescription, dao);

        create(response, dao, Collections.singletonList(item));
        return item;
    }


    public void multipleCreate(Response response, List<T> items, Crud<T> dao, ModelDescription<T> modelDescription) throws Exception {
        if (ClassUtils.hasIntegrityRules(dao.getType())) {
            new AtomicIntegrityEvaluator<T>().evaluate(Lists.newArrayList(Iterables.toArray(items, dao.getType())));
        }

        for (T item : items) {
            evaluate(item, modelDescription, dao);
        }

        create(response, dao, items);
    }

    private void evaluate(T item, ModelDescription<T> modelDescription, Crud<T> dao) throws Exception {
        for (ObjectEvaluator<T> objectEvaluator : modelDescription.getObjectEvaluators()) {
            objectEvaluator.evaluate(item, modelDescription.getModelType());
        }
        new IntegrityEvaluatorImpl<T>().evaluate(item, dao, true);


        modelDescription.getIdentityProvider().provideId(item, dao);

    }

    private void create(Response response, Crud<T> dao, Iterable<T> items) {
        dao.create(items);
        response.status(200);
        response.result("OK");
    }

    @Override
    public void handleCreate(HttpContext context, Crud<T> dao, ModelDescription<T> modelDescription, ObjectMapper objectMapper) throws Exception {
        String body = context.request().body();

        try {
            JavaType type = objectMapper.getTypeFactory().constructCollectionType(List.class, dao.getType());

            final List<T> ts = objectMapper.readValue(body, type);
            multipleCreate(context.response(), ts, dao, modelDescription);
        } catch (JsonMappingException e) {

            T item = objectMapper.readValue(body, dao.getType());
            singleCreate(context.response(), item, dao, modelDescription);
        }
    }
}
