package com.elepy.routes;

import com.elepy.concepts.ObjectEvaluator;
import com.elepy.dao.Crud;
import com.elepy.di.ElepyContext;
import com.elepy.http.HttpContext;

import java.util.List;


public interface DeleteHandler<T> {
    /**
     * This handles the functionality of model deletion.
     *
     * @param crud             The crud implementation
     * @param elepy            The elepy context
     * @param objectEvaluators The list of evaluators
     * @param clazz            The class type
     * @throws Exception you can throw any exception and Elepy handles them nicely.
     * @see com.elepy.exceptions.ElepyException
     * @see com.elepy.exceptions.ElepyErrorMessage
     */
    void handleDelete(HttpContext context, Crud<T> crud, ElepyContext elepy, List<ObjectEvaluator<T>> objectEvaluators, Class<T> clazz) throws Exception;

}
