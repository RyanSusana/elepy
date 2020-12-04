package com.elepy.handlers;

import com.elepy.exceptions.ElepyException;
import com.elepy.http.HttpContext;
import com.elepy.models.ModelContext;

public final class DisabledHandler<T> implements ActionHandler<T> {

    @Override
    public void handle(Context<T> ctx) {
 final var context = ctx.http();
 final var modelContext = ctx.model();
        throw new ElepyException("Not found", 404);
    }
}
