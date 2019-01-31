package com.elepy.dao.jongo;

import com.elepy.annotations.Inject;
import com.elepy.annotations.RestModel;
import com.elepy.dao.Crud;
import com.elepy.dao.CrudProvider;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mongodb.DB;

public class MongoProvider implements CrudProvider {

    @Inject
    private DB elepy;

    @Inject
    private ObjectMapper objectMapper;

    @Override
    public <T> Crud<T> crudFor(Class<T> type) {
        final RestModel model = type.getAnnotation(RestModel.class);

        final String slug = model.slug();

        final String[] split = slug.split("/");

        return new DefaultMongoDao<>(elepy, split[split.length - 1], type, null, objectMapper);
    }
}
