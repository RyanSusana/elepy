package com.elepy.handlers;

import com.elepy.dao.Crud;
import com.elepy.exceptions.ElepyException;
import com.elepy.exceptions.Message;
import com.elepy.http.HttpContext;
import com.elepy.http.Request;
import com.elepy.models.ModelContext;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.Serializable;

/**
 * A helper class for developers to easily handle the update of objects.
 *
 * @param <T> the model you're updating
 * @see com.elepy.annotations.Update
 * @see DefaultUpdate
 * @see UpdateHandler
 */
public abstract class SimpleUpdate<T> extends DefaultUpdate<T> {


    @Override
    public void handleUpdatePut(HttpContext context, Crud<T> dao, ModelContext<T> modelContext, ObjectMapper objectMapper) throws Exception {

        final Serializable id = context.modelId();

        final T before = dao.getById(id).orElseThrow(() -> new ElepyException("This item doesn't exist and therefor can't be updated"));

        T updatedObjectFromRequest = updatedObjectFromRequest(before,
                context.request(),
                objectMapper,
                modelContext.getModel());

        beforeUpdate(before, updatedObjectFromRequest, context.request(), dao);


        final T updated =
                evaluateAndUpdate(context,
                        updatedObjectFromRequest,
                        modelContext
                );
        afterUpdate(before, updated, dao);

        context.response().status(200);
        context.response().result(Message.of("Successfully updated item", 200));
    }

    @Override
    public void handleUpdatePatch(HttpContext context, Crud<T> crud, ModelContext<T> modelContext, ObjectMapper objectMapper) throws Exception {
        super.handleUpdatePut(context, crud, modelContext, objectMapper);
    }

    /**
     * What happens before you update a model. Throw an exception to cancel the update.
     *
     * @param beforeVersion The object before the update
     * @param crud          The crud implementation
     * @throws Exception you can throw any exception and Elepy handles them nicely.
     * @see ElepyException
     * @see com.elepy.exceptions.ElepyErrorMessage
     */
    public abstract void beforeUpdate(T beforeVersion, T updatedVersion, Request httpRequest, Crud<T> crud) throws Exception;

    /**
     * What happens after you update a model.
     *
     * @param beforeVersion  The object before the update
     * @param updatedVersion The object after the update
     * @param crud           The crud implementation
     */
    public abstract void afterUpdate(T beforeVersion, T updatedVersion, Crud<T> crud);
}
