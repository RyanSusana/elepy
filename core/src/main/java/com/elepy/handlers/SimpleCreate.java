package com.elepy.handlers;

import com.elepy.dao.Crud;
import com.elepy.exceptions.ElepyException;
import com.elepy.exceptions.Message;
import com.elepy.http.HttpContext;
import com.elepy.http.Request;
import com.elepy.models.ModelContext;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.List;

/**
 * A helper class for developers to easily handle the creation of objects.
 *
 * @param <T> the model you're updating
 * @see com.elepy.annotations.Create
 * @see DefaultCreate
 */
public abstract class SimpleCreate<T> extends DefaultCreate<T> {

    @Override
    public void handle(Context<T> ctx) throws Exception {
        final var context = ctx.http();
        final var modelContext = ctx.model();

        try {

            var objectMapper = context.elepy().objectMapper();
            String body = context.request().body();

            T item = objectMapper.readValue(body, modelContext.getModelType());

            beforeCreate(item, context.request(), ctx.crud());

            super.singleCreate(context, item, ctx.crud(), modelContext);

            afterCreate(item, ctx.crud());
            context.response().status(200);
            context.response().result(Message.of("Successfully created item", 200).withProperty("createdRecords", List.of(item)));

        } catch (JsonMappingException e) {
            throw new ElepyException("Error mapping SimpleCreate: " + e.getMessage());
        }
    }

    /**
     * What happens before you singleCreate a model. Throw an exception to cancel the creation.
     *
     * @param objectForCreation The object before you singleCreate it
     * @param crud              the crud implementation
     * @param httpRequest       the HTTP request
     * @throws Exception you can throw any exception and Elepy handles them nicely.
     * @see ElepyException
     * @see com.elepy.exceptions.ElepyException
     */
    public abstract void beforeCreate(T objectForCreation, Request httpRequest, Crud<T> crud) throws Exception;

    /**
     * What happens after you singleCreate a model.
     *
     * @param createdObject The object after you created it
     * @param crud          the crud implementation
     */
    public abstract void afterCreate(T createdObject, Crud<T> crud);
}
