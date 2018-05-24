package com.elepy.dao;

import com.elepy.models.Schema;
import com.mongodb.DB;
import org.jongo.Jongo;
import org.jongo.MongoCollection;

import java.util.HashMap;
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
    public long count(String query, Object... parameters) {
        return 0;
    }

//    @Override
//    public List<Map<String, Object>> get() {
//        MongoCursor<Map<String, Object>> models = collection().find().map(
//                dbObject -> {
//                    Map dbo = dbObject.toMap();
//
//                    Map<String, Object> map = new HashMap<>();
//                    for (Object o : dbo.keySet()) {
//                        if (o instanceof String) {
//                            String key = (String) o;
//                            if (key.equals("_id")) {
//                                key = "id";
//                            }
//                            map.put(key, dbo.get(o));
//                        }
//                    }
//                    return map;
//                }
//        );
//
//        return Lists.newArrayList(models.iterator());
//    }

    @Override
    public Page<Map<String, Object>> get(PageSetup pageSetup) {
        return null;
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
    public Page<Map<String, Object>> search(SearchSetup search, PageSetup pageSetup) {
        return null;
    }

    @Override
    public Page<Map<String, Object>> search(String query, Object... params) {
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
