package com.elepy.dao.jongo;

import com.elepy.Elepy;
import com.elepy.annotations.RestModel;
import com.elepy.dao.Crud;
import com.elepy.dao.CrudProvider;
import com.mongodb.DB;

public class MongoProvider<T> implements CrudProvider<T> {

    public MongoProvider() {
    }

    @Override
    public Crud<T> crudFor(Class<T> type, Elepy elepy) {
        final RestModel model = type.getAnnotation(RestModel.class);

        final String slug = model.slug();

        final String[] split = slug.split("/");

        return new MongoDao<T>(elepy.getSingleton(DB.class), split[split.length-1], type);
    }
}
