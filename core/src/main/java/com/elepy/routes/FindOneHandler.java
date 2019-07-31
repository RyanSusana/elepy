package com.elepy.routes;

import com.elepy.dao.Crud;
import com.elepy.http.HttpContext;
import com.elepy.models.ModelContext;
import com.fasterxml.jackson.databind.ObjectMapper;

public interface FindOneHandler<T> extends BaseHandler<T> {

    /**
     * This handles the functionality of finding a model by it's ID, or some self-defined domain specification
     * <p>
     * e.g GET /model/:id
     *
     * @param crud The crud implementation
     * @throws Exception you can throw any exception and Elepy handles them nicely.
     * @see com.elepy.exceptions.ElepyException
     * @see com.elepy.exceptions.ElepyErrorMessage
     */
    void handleFindOne(HttpContext context, Crud<T> crud, ModelContext<T> modelContext, ObjectMapper objectMapper) throws Exception;

}
