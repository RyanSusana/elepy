package com.elepy.handlers;

import com.elepy.dao.Crud;
import com.elepy.evaluators.AtomicIntegrityEvaluator;
import com.elepy.evaluators.DefaultIntegrityEvaluator;
import com.elepy.evaluators.EvaluationType;
import com.elepy.evaluators.ObjectEvaluator;
import com.elepy.exceptions.Message;
import com.elepy.http.HttpContext;
import com.elepy.http.Response;
import com.elepy.models.ModelContext;
import com.elepy.utils.ReflectionUtils;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;

import java.util.Collections;
import java.util.List;

public class DefaultCreate<T> implements CreateHandler<T> {


    public T singleCreate(Response response, T item, Crud<T> dao, ModelContext<T> modelContext) throws Exception {
        evaluate(item, modelContext, dao);

        create(response, dao, Collections.singletonList(item));
        return item;
    }


    public void multipleCreate(Response response, List<T> items, Crud<T> dao, ModelContext<T> modelContext) throws Exception {
        if (ReflectionUtils.hasIntegrityRules(dao.getType())) {
            new AtomicIntegrityEvaluator<T>().evaluate(Lists.newArrayList(Iterables.toArray(items, dao.getType())));
        }

        for (T item : items) {
            evaluate(item, modelContext, dao);
        }

        create(response, dao, items);
    }

    private void evaluate(T item, ModelContext<T> modelContext, Crud<T> dao) throws Exception {
        for (ObjectEvaluator<T> objectEvaluator : modelContext.getObjectEvaluators()) {
            objectEvaluator.evaluate(item);
        }

        modelContext.getIdentityProvider().provideId(item, dao);
        new DefaultIntegrityEvaluator<T>(modelContext).evaluate(item, EvaluationType.CREATE);
    }

    private void create(Response response, Crud<T> dao, Iterable<T> items) {
        dao.create(items);
        response.status(200);
        response.result(Message.of("Successfully created item", 201));
    }

    @Override
    public void handleCreate(HttpContext context, Crud<T> dao, ModelContext<T> modelContext, ObjectMapper objectMapper) throws Exception {
        String body = context.request().body();

        try {
            JavaType type = objectMapper.getTypeFactory().constructCollectionType(List.class, dao.getType());

            final List<T> ts = objectMapper.readValue(body, type);
            multipleCreate(context.response(), ts, dao, modelContext);
        } catch (JsonMappingException e) {

            T item = objectMapper.readValue(body, dao.getType());
            singleCreate(context.response(), item, dao, modelContext);
        }
    }
}
