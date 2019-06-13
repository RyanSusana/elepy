package com.elepy.routes;

import com.elepy.dao.Crud;
import com.elepy.describers.ModelContext;
import com.elepy.exceptions.ElepyException;
import com.elepy.exceptions.Message;
import com.elepy.http.HttpContext;
import com.elepy.http.Request;
import com.elepy.utils.ReflectionUtils;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.Serializable;
import java.util.Optional;

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
        String body = context.request().body();

        T item = objectMapper.readValue(body, modelContext.getModelType());

        final Optional<Serializable> id = ReflectionUtils.getId(item);
        if (!id.isPresent()) {
            throw new ElepyException("This item doesn't can't be identified.");
        }

        final Optional<T> before = dao.getById(id.get());

        if (!before.isPresent()) {
            throw new ElepyException("This item doesn't exist and therefor can't be updated");
        }

        beforeUpdate(before.get(), context.request(), dao);

        T updatedObjectFromRequest = updatedObjectFromRequest(before.get(),
                context.request(),
                objectMapper,
                modelContext.getModelType());

        final T updated =
                update(before.get(),
                        updatedObjectFromRequest,
                        dao,
                        modelContext.getObjectEvaluators(),
                        modelContext.getModelType());
        afterUpdate(before.get(), updated, dao);

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
    public abstract void beforeUpdate(T beforeVersion, Request httpRequest, Crud<T> crud) throws Exception;

    /**
     * What happens after you update a model.
     *
     * @param beforeVersion  The object before the update
     * @param updatedVersion The object after the update
     * @param crud           The crud implementation
     */
    public abstract void afterUpdate(T beforeVersion, T updatedVersion, Crud<T> crud);
}
