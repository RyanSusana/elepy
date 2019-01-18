package com.elepy.routes;

import com.elepy.concepts.ObjectEvaluator;
import com.elepy.dao.Crud;
import com.elepy.di.ElepyContext;
import com.elepy.exceptions.ElepyException;
import com.elepy.utils.ClassUtils;
import spark.Request;
import spark.Response;

import java.util.List;
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
    public void handleUpdate(Request request, Response response, Crud<T> dao, ElepyContext elepy, List<ObjectEvaluator<T>> objectEvaluators, Class<T> clazz) throws Exception {
        String body = request.body();

        T item = elepy.getObjectMapper().readValue(body, clazz);

        final Optional<String> id = ClassUtils.getId(item);
        if (!id.isPresent()) {
            throw new ElepyException("This item doesn't can't be identified.");
        }

        final Optional<T> before = dao.getById(id.get());

        if (!before.isPresent()) {
            throw new ElepyException("This item doesn't exist and therefor can't be updated");
        }

        beforeUpdate(before.get(), dao, elepy);

        T updatedObjectFromRequest = updatedObjectFromRequest(before.get(),
                request,
                elepy.getObjectMapper(),
                clazz);

        final T updated =
                this.update(before.get(),
                        updatedObjectFromRequest,
                        dao,
                        objectEvaluators,
                        clazz);
        afterUpdate(before.get(), updated, dao, elepy);

        response.status(200);
        response.body("OK");
    }


    /**
     * What happens before you update a model. Throw an exception to cancel the update.
     *
     * @param beforeVersion The object before the update
     * @param crud          The crud implementation
     * @param elepy         the context where you can get context objects
     * @throws Exception you can throw any exception and Elepy handles them nicely.
     * @see ElepyException
     * @see com.elepy.exceptions.ElepyErrorMessage
     */
    public abstract void beforeUpdate(T beforeVersion, Crud<T> crud, ElepyContext elepy) throws Exception;

    /**
     * What happens after you update a model.
     *
     * @param beforeVersion The object before the update
     * @param updatedVersion The object after the update
     * @param crud The crud implementation
     * @param elepy the context where you can get context objects
     */
    public abstract void afterUpdate(T beforeVersion, T updatedVersion, Crud<T> crud, ElepyContext elepy);
}
