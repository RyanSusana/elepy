package com.elepy.handlers;

import com.elepy.http.HttpContext;
import com.elepy.models.ModelContext;

public interface ActionHandler<T> {

    void handle(Context<T> ctx) throws Exception;

}
