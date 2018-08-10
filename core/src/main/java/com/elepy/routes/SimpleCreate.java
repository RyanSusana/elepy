package com.elepy.routes;

import com.elepy.Elepy;
import com.elepy.concepts.ObjectEvaluator;
import com.elepy.dao.Crud;
import com.elepy.exceptions.RestErrorMessage;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import spark.Request;
import spark.Response;

import java.util.List;

public abstract class SimpleCreate<T> extends DefaultCreate<T> {

    @Override
    public void handle(Request request, Response response, Crud<T> dao, Elepy elepy, List<ObjectEvaluator<T>> objectEvaluators, Class<T> clazz) throws Exception {
        super.handle(request, response, dao, elepy, objectEvaluators, clazz);
        String body = request.body();

        ObjectMapper objectMapper = elepy.getObjectMapper();
        try {

            T item = objectMapper.readValue(body, dao.getType());

            onCreate(item, dao, elepy);


        } catch (JsonMappingException e) {
            throw new RestErrorMessage("MultipleCreate not supported with SimpleCreate");
        }
    }

    public abstract void onCreate(T createdObject, Crud<T> crud, Elepy elepy);
}
