package com.elepy.mongo;

import com.elepy.dao.Crud;
import com.elepy.dao.querymodel.Expression;
import com.elepy.dao.querymodel.Query;
import com.elepy.dao.querymodel.SortOption;
import com.elepy.exceptions.ElepyConfigException;
import com.elepy.exceptions.ElepyException;
import com.elepy.models.Property;
import com.elepy.models.Schema;
import com.elepy.mongo.annotations.MongoIndex;
import com.elepy.utils.ReflectionUtils;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mongodb.BasicDBObject;
import com.mongodb.MongoSocketException;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.IndexOptions;
import com.mongodb.client.model.Indexes;
import com.mongodb.client.model.Sorts;
import org.bson.conversions.Bson;
import org.mongojack.internal.MongoJackModule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public class MongoDao<T> implements Crud<T> {

    private final Schema<T> schema;
    private final ObjectMapper objectMapper;
    private final MongoCollection<T> mongoCollection;

    private static final Logger logger = LoggerFactory.getLogger("mongo");

    private final QueryBuilder<T> queryBuilder;
    private final MongoDatabase database;


    public MongoDao(MongoDatabase database, final String collectionName, final Schema<T> schema) {
        this.database = database;
        this.schema = schema;
        this.objectMapper = MongoJackModule.configure(CustomJacksonModule.configure(new ObjectMapper()));

        ElepyCodecRegistry jacksonCodecRegistry = new ElepyCodecRegistry(objectMapper, null);
        jacksonCodecRegistry.addCodecForClass(schema.getJavaClass());
        this.mongoCollection =
                database.getCollection(collectionName.replaceAll("/", ""))
                        .withDocumentClass(schema.getJavaClass())
                        .withCodecRegistry(jacksonCodecRegistry);
        this.queryBuilder = new QueryBuilder<>(schema);
        createIndexes();

    }


    public MongoCollection<T> getMongoCollection() {
        return mongoCollection;
    }

    private void createIndexes() {
        try {
            Arrays.stream(schema.getJavaClass().getAnnotationsByType(MongoIndex.class))
                    .forEach(this::createIndex);


            schema.getProperties().stream().filter(Property::isUnique)
                    .forEach(property -> mongoCollection.createIndex(new BasicDBObject(property.getName(), 1)));
        } catch (MongoSocketException e) {
            logger.error("Failed at creating index", e);
        }

    }

    @Override
    public List<T> find(Query query) {

        query.purge();

        final List<Bson> sortSpec = query.getSortingSpecification().getMap().entrySet().stream().map(entry -> {
            if (entry.getValue().equals(SortOption.ASCENDING)) {
                return Sorts.ascending(entry.getKey());
            } else {
                return Sorts.descending(entry.getKey());
            }
        }).collect(Collectors.toList());
        final var expression = new QueryBuilder<>(schema).expression(query.getExpression());
        return mongoCollection.find(expression).limit(query.getLimit()).skip(query.getSkip()).sort(Sorts.orderBy(sortSpec)).into(new ArrayList<>());
    }

    @Override
    public Optional<T> getById(Serializable id) {
        return Optional.ofNullable(mongoCollection.find(Filters.eq("_id", id)).first());
    }


    @Override
    public void update(T item) {
        mongoCollection.replaceOne(Filters.eq("_id", getId(item)), item);
    }

    @Override
    public void create(T item) {

        idQuery(item);
        mongoCollection.insertOne(item);
    }

    @Override
    public void create(T... items) {
        for (T item : items) {
            idQuery(item);
        }
        mongoCollection.insertMany(List.of(items));
    }

    private Bson idQuery(T item) {

        Optional<Serializable> idMaybe = ReflectionUtils.getId(item);
        if (idMaybe.isEmpty()) {

            try {
                throw new ElepyException(String.format("No Identifier provided to the object: %s", objectMapper.writeValueAsString(item)));
            } catch (JsonProcessingException e) {
                throw ElepyException.internalServerError(e);
            }
        }

        var id = idMaybe.get();
        return idQuery(id);
    }

    private Bson idQuery(Serializable id) {
        return Filters.eq("_id", id);
    }


    @Override
    public List<T> getAll() {
        return mongoCollection.find().into(new ArrayList<>());
    }

    @Override
    public void deleteById(Serializable id) {
        mongoCollection.deleteOne(Filters.eq("_id", id));
    }

    @Override
    public void delete(Expression expression) {
        mongoCollection.deleteMany(queryBuilder.expression(expression));
    }

    @Override
    public long count(Query query) {
        query.purge();
        return mongoCollection.countDocuments(queryBuilder.expression(query.getExpression()));
    }

    @Override
    public Schema<T> getSchema() {
        return schema;
    }

    @Override
    public ObjectMapper getObjectMapper() {
        return objectMapper;
    }


    private void createIndex(MongoIndex annotation) {

        if (annotation.properties().length == 0) {
            throw new ElepyConfigException("No properties specified in MongoIndex");
        }


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

        mongoCollection.createIndex(compoundIndex, indexOptions);
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
}
