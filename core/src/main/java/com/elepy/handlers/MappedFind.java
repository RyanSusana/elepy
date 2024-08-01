package com.elepy.handlers;

import com.elepy.crud.Crud;
import com.elepy.http.Request;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * This is a combination of {@link MappedFindMany} and {@link MappedFindMany}.
 * <p>
 * Use this class if you want to always map from T to R. Regardless if you are finding one or many.
 * <p>
 * A good use case is, mapping Product to ProductDTO
 * Another is, mapping a User to a User(but taking away the password hash).
 *
 * @param <T> The RestModel type
 * @param <R> The type you want to map to.
 */
public abstract class MappedFind<T, R extends T> extends MappedFindOne<T, R> implements ActionHandler<T> {

    private final MappedFindMany<T, R> mappedFindMany = new DefaultMappedFindMany();

    public abstract R map(T object, Request request, Crud<T> crud);

    @Override
    public void handle(HandlerContext<T> ctx) throws Exception {
        final var context = ctx.http();
        final var modelContext = ctx.model();

        //No recordId's specified
        final var ids = context.recordIds();
        ids.removeIf(Objects::isNull);
        if (ids.isEmpty()) {
            mappedFindMany.handle(ctx);
        } else {
            super.handle(ctx);
        }
    }


    private class DefaultMappedFindMany extends com.elepy.handlers.MappedFindMany<T, R> {
        @Override
        public List<R> mapValues(List<? extends T> typeStream, Request request, Crud<T> crud) {
            return typeStream.stream().map(t -> MappedFind.this.map(t, request, crud)).collect(Collectors.toList());
        }
    }

}
