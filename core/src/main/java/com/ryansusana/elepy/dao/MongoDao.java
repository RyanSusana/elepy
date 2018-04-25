package com.ryansusana.elepy.dao;


import com.google.common.collect.Lists;
import com.mongodb.DB;
import com.ryansusana.elepy.annotations.RestModel;
import com.ryansusana.elepy.annotations.Searchable;
import com.ryansusana.elepy.annotations.Unique;
import com.ryansusana.elepy.concepts.FieldUtils;
import com.ryansusana.elepy.concepts.IdProvider;
import com.ryansusana.elepy.models.IdGenerationType;
import com.ryansusana.elepy.models.RestErrorMessage;
import org.codehaus.jackson.map.ObjectMapper;
import org.jongo.Find;
import org.jongo.Jongo;
import org.jongo.Mapper;
import org.jongo.MongoCollection;
import org.jongo.marshall.jackson.JacksonMapper;
import org.jongo.marshall.jackson.oid.MongoId;

import java.lang.reflect.Field;
import java.util.*;
import java.util.regex.Pattern;

public class MongoDao<T> implements Crud<T> {

    private final Jongo jongo;
    private final Class<? extends T> classType;
    private final String collectionName;
    private final ObjectMapper objectMapper;


    public MongoDao(final DB db, final String collectionName, final Class<? extends T> classType) {
        this(db, collectionName, JacksonMapper.Builder.jacksonMapper().build(), classType);
    }

    public MongoDao(final DB db, final String collectionName, Mapper objectMapper, final Class<? extends T> classType) {
        this.jongo = new Jongo(db, objectMapper);
        this.objectMapper = new ObjectMapper();
        this.classType = classType;
        this.collectionName = collectionName.replaceAll("/", "");

    }

    protected MongoCollection collection() {
        return jongo.getCollection(collectionName);
    }

    @Override
    public List<T> getAll() {

        return Lists.newArrayList(collection().find().as(classType).iterator());
    }


    @Override
    public Optional<T> getById(final String id) {
        return Optional.ofNullable(collection().findOne("{_id: #}", id).as(classType));
    }


    @Override
    public long count(String query, Object... parameters) {
        return collection().count(query, parameters);
    }


    @Override
    public List<T> search(SearchSetup query) {

        final List<Field> searchableFields = getSearchableFields();
        System.out.println(searchableFields.size());
        List<Map<String, String>> expressions = new ArrayList<>();
        Map<String, Object> qmap = new HashMap<>();
        Pattern[] hashs = new Pattern[searchableFields.size()];
        final Pattern pattern = Pattern.compile(".*" + query.getQuery() + ".*", Pattern.CASE_INSENSITIVE);
        for (int i = 0; i < hashs.length; i++) {
            hashs[i] = pattern;
        }
        for (Field field : searchableFields) {
            Map<String, String> keyValue = new HashMap<>();
            keyValue.put(FieldUtils.getPropertyName(field), "#");
            expressions.add(keyValue);
        }
        qmap.put("$or", expressions);
        try {

            Find find = query.getQuery() != null ? collection().find(objectMapper.writeValueAsString(qmap).replaceAll("\"#\"", "#"), (Object[]) hashs) : collection().find();
            if (query.getSortBy() != null && query.getSortOption() != null) {
                find = find.sort(String.format("{%s: %d}", query.getSortBy(), query.getSortOption().getVal()));
            }
            return Lists.newArrayList(find.as(classType).iterator());
        } catch (Exception e) {
            e.printStackTrace();
            throw new RestErrorMessage(e.getMessage());
        }

    }

    @Override
    public List<T> search(String query, Object... params) {
        return Lists.newArrayList(collection().find(query,params).as(classType).iterator());
    }


    private List<Field> getSearchableFields() {
        return FieldUtils.searchForFieldsWithAnnotation(classType, Searchable.class, MongoId.class, Unique.class);
    }


    @Override
    public void delete(String id) {
        collection().remove("{_id: #}", id);
    }

    @Override
    public void update(T item) {

        collection().update("{_id: #}", getId(item)).with(item);

    }

    @Override
    public void create(T item) {
        try {
            final Field idField = IdProvider.getIdField(item);
            final RestModel annotation = item.getClass().getAnnotation(RestModel.class);
            if (!annotation.idGenerator().equals(IdGenerationType.NONE)) {
                assert idField != null;
                idField.setAccessible(true);
                idField.set(item, annotation.idGenerator().generateId());
            }

            collection().insert(item);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }

    }

    @Override
    public String getId(T item) {
        String id = FieldUtils.getId(item);
        if (id == null) {
            throw new IllegalStateException(item.getClass().getName() + ": has no annotation id. You must annotate the class with MongoId and if no id generator is specified, you must generate your own.");
        }
        return id;
    }


    public Jongo getJongo() {
        return this.jongo;
    }

    public Class<? extends T> getClassType() {
        return this.classType;
    }

    public String getCollectionName() {
        return this.collectionName;
    }
}
