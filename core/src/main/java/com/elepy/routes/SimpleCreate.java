package com.elepy.routes;

import com.elepy.concepts.ObjectEvaluator;
import com.elepy.dao.Crud;
import com.elepy.di.ElepyContext;
import com.elepy.exceptions.ElepyException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import spark.Request;
import spark.Response;

import java.util.List;

public abstract class SimpleCreate<T> extends DefaultCreate<T> {

    @Override
    public void handle(Request request, Response response, Crud<T> dao, ElepyContext elepy, List<ObjectEvaluator<T>> objectEvaluators, Class<T> clazz) throws Exception {

        try {

            String body = request.body();

            ObjectMapper objectMapper = elepy.getObjectMapper();
            T item = objectMapper.readValue(body, dao.getType());

            beforeCreate(item, dao, elepy);

            super.handle(request, response, dao, elepy, objectEvaluators, clazz);

            afterCreate(item, dao, elepy);


        } catch (JsonMappingException e) {
            throw new ElepyException("MultipleCreate not supported with SimpleCreate");
        }
    }

    public abstract void beforeCreate(T objectForCreation, Crud<T> crud, ElepyContext elepy) throws Exception;

    public abstract void afterCreate(T createdObject, Crud<T> crud, ElepyContext elepy);
}
