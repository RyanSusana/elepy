package com.elepy.dao.jongo;

import com.elepy.annotations.RestModel;
import com.elepy.dao.Crud;
import com.elepy.dao.CrudProvider;
import com.elepy.di.ElepyContext;
import com.mongodb.DB;

public class MongoProvider implements CrudProvider {

    @Override
    public <T> Crud<T> crudFor(Class<T> type, ElepyContext elepy) {
        final RestModel model = type.getAnnotation(RestModel.class);

        final String slug = model.slug();

        final String[] split = slug.split("/");

        return new DefaultMongoDao<>(elepy.getDependency(DB.class), split[split.length - 1], type, null, elepy.getObjectMapper());
    }
}
