package com.ryansusana.elepy.dao;

import com.google.common.collect.Lists;
import com.mongodb.DB;
import com.ryansusana.elepy.models.Schema;
import org.jongo.Jongo;
import org.jongo.MongoCollection;
import org.jongo.MongoCursor;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class MongoSchemaDao implements Crud<Map<String, Object>> {
    private final Jongo jongo;
    private final Schema schema;
    private final String collectionName;

    public MongoSchemaDao(DB jongo, Schema schema) {
        this.jongo = new Jongo(jongo);
        this.schema = schema;
        this.collectionName = schema.getSlug().replaceAll("/", "");
    }

    protected MongoCollection collection() {
        return jongo.getCollection(collectionName);
    }

    public String getId(Map<String, Object> object) {
        final Object id = object.get("id");
        if (id instanceof String) {
            return (String) id;
        }
        throw new IllegalStateException(object.getClass().getName() + ": has no annotation id");
    }

    @Override
    public List<Map<String, Object>> getAll() {
        MongoCursor<Map<String, Object>> models = collection().find().map(
                dbObject -> {
                    Map dbo = dbObject.toMap();

                    Map<String, Object> map = new HashMap<>();
                    for (Object o : dbo.keySet()) {
                        if (o instanceof String) {
                            String key = (String) o;
                            if (key.equals("_id")) {
                                key = "id";
                            }
                            map.put(key, dbo.get(o));
                        }
                    }
                    return map;
                }
        );

        return Lists.newArrayList(models.iterator());
    }

    @Override
    public Optional<Map<String, Object>> getById(String id) {
        return Optional.ofNullable(collection().findOne("{_id: #}", id).map(dbObject -> {
            Map dbo = dbObject.toMap();

            Map<String, Object> map = new HashMap<>();
            for (Object o : dbo.keySet()) {
                if (o instanceof String) {
                    map.put((String) o, dbo.get(o));
                }
            }
            return map;
        }));
    }

    @Override
    public List<Map<String, Object>> search(SearchSetup query) {
        return null;
    }

    @Override
    public void delete(String id) {
        collection().remove("{_id: #}", id);
    }

    @Override
    public void update(Map<String, Object> item) {
        collection().update("{_id: #}", getId(item)).with(item);
    }

    @Override
    public void create(Map<String, Object> item) {
        collection().insert(item);
    }

}
