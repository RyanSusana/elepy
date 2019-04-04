package com.elepy.routes;

import com.elepy.dao.Crud;
import com.elepy.describers.ModelDescription;
import com.elepy.exceptions.ElepyException;
import com.elepy.http.HttpContext;
import com.elepy.utils.ClassUtils;
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
    public void handleUpdatePut(HttpContext context, Crud<T> dao, ModelDescription<T> modelDescription, ObjectMapper objectMapper) throws Exception {
        String body = context.request().body();

        T item = objectMapper.readValue(body, modelDescription.getModelType());

        final Optional<Serializable> id = ClassUtils.getId(item);
        if (!id.isPresent()) {
            throw new ElepyException("This item doesn't can't be identified.");
        }

        final Optional<T> before = dao.getById(id.get());

        if (!before.isPresent()) {
            throw new ElepyException("This item doesn't exist and therefor can't be updated");
        }

        beforeUpdate(before.get(), dao);

        T updatedObjectFromRequest = updatedObjectFromRequest(before.get(),
                context.request(),
                objectMapper,
                modelDescription.getModelType());

        final T updated =
                update(before.get(),
                        updatedObjectFromRequest,
                        dao,
                        modelDescription.getObjectEvaluators(),
                        modelDescription.getModelType());
        afterUpdate(before.get(), updated, dao);

        context.response().status(200);
        context.response().result("OK");
    }

    @Override
    public void handleUpdatePatch(HttpContext context, Crud<T> crud, ModelDescription<T> modelDescription, ObjectMapper objectMapper) throws Exception {
        super.handleUpdatePut(context, crud, modelDescription, objectMapper);
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
    public abstract void beforeUpdate(T beforeVersion, Crud<T> crud) throws Exception;

    /**
     * What happens after you update a model.
     *
     * @param beforeVersion  The object before the update
     * @param updatedVersion The object after the update
     * @param crud           The crud implementation
     */
    public abstract void afterUpdate(T beforeVersion, T updatedVersion, Crud<T> crud);
}
