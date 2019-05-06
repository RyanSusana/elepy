package com.elepy.routes;

import com.elepy.dao.Crud;
import com.elepy.describers.ModelContext;
import com.elepy.http.HttpContext;
import com.elepy.http.Request;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.List;
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
public abstract class MappedFind<T, R> extends MappedFindOne<T, R> implements FindManyHandler<T> {

    private MappedFindMany<T, R> mappedFindMany = new DefaultMappedFindMany();

    public abstract R map(T object, Request request, Crud<T> crud);

    @Override
    public void handleFindMany(HttpContext context, Crud<T> crud, ModelContext<T> modelContext, ObjectMapper objectMapper) throws Exception {
        mappedFindMany.handleFindMany(context, crud, modelContext, objectMapper);
    }


    private class DefaultMappedFindMany extends com.elepy.routes.MappedFindMany<T, R> {
        @Override
        public List<R> mapValues(List<T> typeStream, Request request, Crud<T> crud) {
            return typeStream.stream().map(t -> MappedFind.this.map(t, request, crud)).collect(Collectors.toList());
        }
    }

}
