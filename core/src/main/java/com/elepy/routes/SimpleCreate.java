package com.elepy.routes;

import com.elepy.dao.Crud;
import com.elepy.describers.ModelContext;
import com.elepy.exceptions.ElepyException;
import com.elepy.exceptions.Message;
import com.elepy.http.HttpContext;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * A helper class for developers to easily handle the creation of objects.
 *
 * @param <T> the model you're updating
 * @see com.elepy.annotations.Create
 * @see DefaultCreate
 * @see CreateHandler
 */
public abstract class SimpleCreate<T> extends DefaultCreate<T> {

    @Override
    public void handleCreate(HttpContext context, Crud<T> dao, ModelContext<T> modelContext, ObjectMapper objectMapper) throws Exception {

        try {

            String body = context.request().body();

            T item = objectMapper.readValue(body, dao.getType());

            beforeCreate(item, dao);

            super.handleCreate(context, dao, modelContext, objectMapper);

            afterCreate(item, dao);
            context.response().status(200);
            context.response().result(Message.of("Successfully created item", 200));

        } catch (JsonMappingException e) {
            throw new ElepyException("Error mapping SimpleCreate: " + e.getMessage());
        }
    }

    /**
     * What happens before you singleCreate a model. Throw an exception to cancel the creation.
     *
     * @param objectForCreation The object before you singleCreate it
     * @param crud              the crud implementation
     * @throws Exception you can throw any exception and Elepy handles them nicely.
     * @see ElepyException
     * @see com.elepy.exceptions.ElepyErrorMessage
     */
    public abstract void beforeCreate(T objectForCreation, Crud<T> crud) throws Exception;

    /**
     * What happens after you singleCreate a model.
     *
     * @param createdObject The object after you created it
     * @param crud          the crud implementation
     */
    public abstract void afterCreate(T createdObject, Crud<T> crud);
}
