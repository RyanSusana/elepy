package com.elepy.handlers;

import com.elepy.dao.Crud;
import com.elepy.evaluators.AtomicIntegrityEvaluator;
import com.elepy.evaluators.DefaultIntegrityEvaluator;
import com.elepy.evaluators.EvaluationType;
import com.elepy.evaluators.ObjectEvaluator;
import com.elepy.exceptions.Message;
import com.elepy.http.HttpContext;
import com.elepy.models.ModelContext;
import com.elepy.utils.ReflectionUtils;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;

import java.util.Collections;
import java.util.List;

public class DefaultCreate<T> implements ActionHandler<T> {


    @Override
    public void handle(HttpContext context, ModelContext<T> modelContext) throws Exception {
        String body = context.request().body();

        final var objectMapper = context.elepy().objectMapper();
        final var dao = modelContext.getCrud();

        try {
            JavaType type = objectMapper.getTypeFactory().constructCollectionType(List.class, dao.getType());

            final List<T> ts = objectMapper.readValue(body, type);
            multipleCreate(context, ts, dao, modelContext);
        } catch (JsonMappingException e) {

            T item = objectMapper.readValue(body, dao.getType());
            singleCreate(context, item, dao, modelContext);
        }
    }

    protected void singleCreate(HttpContext context, T item, Crud<T> dao, ModelContext<T> modelContext) throws Exception {
        evaluate(item, modelContext, context, dao);

        create(context, dao, Collections.singletonList(item));
    }


    private void multipleCreate(HttpContext context, List<T> items, Crud<T> dao, ModelContext<T> modelContext) throws Exception {
        if (ReflectionUtils.hasIntegrityRules(dao.getType())) {
            new AtomicIntegrityEvaluator<T>().evaluate(Lists.newArrayList(Iterables.toArray(items, dao.getType())));
        }

        for (T item : items) {
            evaluate(item, modelContext, context, dao);
        }
        create(context, dao, items);
    }

    private void evaluate(T item, ModelContext<T> modelContext, HttpContext context, Crud<T> dao) throws Exception {
        for (ObjectEvaluator<T> objectEvaluator : modelContext.getObjectEvaluators()) {
            objectEvaluator.evaluate(item);
        }

        context.validate(item);

        modelContext.getIdentityProvider().provideId(item, dao);
        new DefaultIntegrityEvaluator<>(modelContext).evaluate(item, EvaluationType.CREATE);
    }

    private void create(HttpContext context, Crud<T> dao, Iterable<T> items) {
        dao.create(items);


        context.status(200);
        context.result(Message.of("Successfully created item(s)", 201).withProperty("createdRecords", items));
    }


}
