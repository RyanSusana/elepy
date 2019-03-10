package com.elepy.routes;

import com.elepy.dao.Crud;
import com.elepy.describers.ModelDescription;
import com.elepy.http.HttpContext;
import com.fasterxml.jackson.databind.ObjectMapper;


public interface UpdateHandler<T> {
    /**
     * This handles the functionality of model updates.
     *
     * e.g PUT /model/:id
     *
     * @param crud The crud implementation
     * @throws Exception you can throw any exception and Elepy handles them nicely.
     * @see com.elepy.exceptions.ElepyException
     * @see com.elepy.exceptions.ElepyErrorMessage
     */
    void handleUpdatePut(HttpContext context, Crud<T> crud, ModelDescription<T> modelDescription, ObjectMapper objectMapper) throws Exception;


    /**
     * This handles the functionality of partial model updates.
     *
     * e.g PATCH /model/:id
     *
     * @param crud The crud implementation
     * @throws Exception you can throw any exception and Elepy handles them nicely.
     * @see com.elepy.exceptions.ElepyException
     * @see com.elepy.exceptions.ElepyErrorMessage
     */
    void handleUpdatePatch(HttpContext context, Crud<T> crud, ModelDescription<T> modelDescription, ObjectMapper objectMapper) throws Exception;

}
