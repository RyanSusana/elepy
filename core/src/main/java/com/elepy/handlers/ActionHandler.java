package com.elepy.handlers;

import com.elepy.http.HttpContext;
import com.elepy.models.ModelContext;

public interface ActionHandler<T> {

    void handle(HttpContext context, ModelContext<T> modelContext) throws Exception;

}
