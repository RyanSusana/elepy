package com.elepy.handlers;

import com.elepy.exceptions.ElepyException;
import com.elepy.http.HttpContext;
import com.elepy.models.ModelContext;

public final class DisabledHandler<T> implements ActionHandler<T> {

    @Override
    public void handle(HttpContext context, ModelContext<T> modelContext) throws Exception {
        throw new ElepyException("Not found", 404);
    }
}
