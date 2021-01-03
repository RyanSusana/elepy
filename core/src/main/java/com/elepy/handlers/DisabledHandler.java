package com.elepy.handlers;

import com.elepy.exceptions.ElepyException;

public final class DisabledHandler<T> implements ActionHandler<T> {

    @Override
    public void handle(HandlerContext<T> ctx) {
 final var context = ctx.http();
 final var modelContext = ctx.model();
        throw ElepyException.notFound();
    }
}
