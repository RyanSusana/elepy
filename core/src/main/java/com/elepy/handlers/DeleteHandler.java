package com.elepy.handlers;

import com.elepy.dao.Crud;
import com.elepy.http.HttpContext;
import com.elepy.models.ModelContext;
import com.fasterxml.jackson.databind.ObjectMapper;


public interface DeleteHandler<T> extends BaseHandler<T> {
    /**
     * This handles the functionality of model deletion.
     *
     * @param crud The crud implementation
     * @throws Exception you can throw any exception and Elepy handles them nicely.
     * @see com.elepy.exceptions.ElepyException
     * @see com.elepy.exceptions.ElepyErrorMessage
     */
    void handleDelete(HttpContext context, Crud<T> crud, ModelContext<T> modelContext, ObjectMapper objectMapper) throws Exception;

}
