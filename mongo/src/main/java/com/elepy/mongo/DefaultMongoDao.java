package com.elepy.mongo;


import com.elepy.exceptions.ElepyConfigException;
import com.elepy.models.Property;
import com.elepy.models.Schema;
import com.elepy.mongo.annotations.MongoIndex;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.MongoSocketException;
import com.mongodb.client.model.IndexOptions;
import com.mongodb.client.model.Indexes;
import org.bson.conversions.Bson;
import org.jongo.Jongo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public class DefaultMongoDao<T> extends MongoDao<T> {

    private final DB db;
    private final Schema<T> schema;
    private final String collectionName;
    private final ObjectMapper objectMapper;
    private final Jongo jongo;

    private static final Logger logger = LoggerFactory.getLogger("mongo");

    public DefaultMongoDao(final DB db, final String collectionName, final Schema<T> schema) {
        this(db, collectionName, schema, new ObjectMapper());
    }


    public DefaultMongoDao(final DB db, final String collectionName, final Schema<T> schema, ObjectMapper objectMapper) {
        this.db = db;
        this.schema = schema;
        this.collectionName = collectionName.replaceAll("/", "");
        this.objectMapper = objectMapper;

        this.jongo = new Jongo(db(), JongoMapperFactory.createMapper());

        createIndexes();

    }


    private void createIndexes() {
        try {
            Arrays.stream(schema.getJavaClass().getAnnotationsByType(MongoIndex.class))
                    .forEach(this::createIndex);

            final var collection = mongoCollection();


            schema.getProperties().stream().filter(Property::isUnique)
                    .forEach(property -> collection.createIndex(new BasicDBObject(property.getName(), 1)));
        } catch (MongoSocketException e) {
            logger.error("Failed at creating index", e);
        }

    }


    private void createIndex(MongoIndex annotation) {

        if (annotation.properties().length == 0) {
            throw new ElepyConfigException("No properties specified in MongoIndex");
        }

        final var collection = mongoCollection();

        final var indexOptions = new IndexOptions();

        if (!isDefault(annotation.text())) {
            indexOptions.textVersion(annotation.text());
        }
        if (!isDefault(annotation.expireAfterSeconds())) {
            indexOptions.expireAfter(annotation.expireAfterSeconds(), TimeUnit.SECONDS);
        }
        if (!isDefault(annotation.name())) {
            indexOptions.name(annotation.name());
        }
        if (!isDefault(annotation.unique())) {
            indexOptions.unique(annotation.unique());
        }
        final var compoundIndex = Indexes.compoundIndex(Arrays
                .stream(annotation.properties())
                .map(this::createIndexField)
                .collect(Collectors.toList()));

        collection.createIndex(compoundIndex, indexOptions);
    }

    private Bson createIndexField(String property) {
        final var split = property.split(":");

        //Ensures property exists
        schema.getProperty(split[0]);

        if (split.length == 1) {
            return Indexes.ascending(property);
        }

        try {
            final var i = Integer.parseInt(split[1]);

            return new BasicDBObject(property, i);
        } catch (NumberFormatException e) {
            throw new ElepyConfigException(String.format("%s is not a valid integer", split[1]), e);
        }
    }

    private boolean isDefault(Object o) {
        if (o instanceof String) {
            return "".equals(o);
        }
        if (o instanceof Boolean) {
            return !((Boolean) o);
        }
        if (o instanceof Long || o instanceof Integer) {
            return ((Number) o).longValue() == -1;
        }
        return false;
    }


    @Override
    Jongo getJongo() {
        return jongo;
    }


    @Override
    public String mongoCollectionName() {
        return collectionName;
    }

    @Override
    public Schema<T> getSchema() {
        return schema;
    }

    @Override
    public ObjectMapper getObjectMapper() {
        return objectMapper;
    }

    @Override
    public DB db() {
        return db;
    }

}
