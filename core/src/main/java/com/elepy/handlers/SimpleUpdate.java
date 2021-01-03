package com.elepy.handlers;

import com.elepy.dao.Crud;
import com.elepy.exceptions.ElepyException;
import com.elepy.exceptions.Message;
import com.elepy.http.Request;

import java.io.Serializable;

/**
 * A helper class for developers to easily handle the update of objects.
 *
 * @param <T> the model you're updating
 * @see com.elepy.annotations.Update
 * @see DefaultUpdate
 */
public abstract class SimpleUpdate<T> extends DefaultUpdate<T> {


    @Override
    public void handle(HandlerContext<T> ctx) throws Exception {
        final var context = ctx.http();
        final var modelContext = ctx.model();

        final Serializable id = context.recordId();

        final var crud = ctx.crud();

        final T before = crud.getById(id).orElseThrow(ElepyException::notFound);

        T updatedObjectFromRequest = updatedObjectFromRequest(before,
                context.request(),
                context.elepy().objectMapper(),
                modelContext.getSchema());

        beforeUpdate(before, updatedObjectFromRequest, context.request(), crud);


        final T updated =
                evaluateAndUpdate(ctx,
                        updatedObjectFromRequest
                );
        afterUpdate(before, updated, crud);

        context.response().status(200);
        context.response().result(Message.of("Successfully updated item", 200));
    }


    /**
     * What happens before you update a model. Throw an exception to cancel the update.
     *
     * @param beforeVersion The object before the update
     * @param crud          The crud implementation
     * @throws Exception you can throw any exception and Elepy handles them nicely.
     * @see ElepyException
     * @see com.elepy.exceptions.ElepyException
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
