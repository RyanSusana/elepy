package com.elepy.routes;

import com.elepy.dao.Crud;
import com.elepy.describers.ModelContext;
import com.elepy.http.HttpContext;
import com.fasterxml.jackson.databind.ObjectMapper;

public interface FindManyHandler<T> {
    /**
     * This handles the functionality of model querying.
     * <p>
     * e.g GET /model
     *
     * @param crud The crud implementation
     * @throws Exception you can throw any exception and Elepy handles them nicely.
     * @see com.elepy.exceptions.ElepyException
     * @see com.elepy.exceptions.ElepyErrorMessage
     */
    void handleFindMany(HttpContext context, Crud<T> crud, ModelContext<T> modelContext, ObjectMapper objectMapper) throws Exception;

}
