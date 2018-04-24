package com.ryansusana.elepy.dao;


import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.collect.Lists;
import com.mongodb.DB;
import com.ryansusana.elepy.annotations.RestModel;
import com.ryansusana.elepy.annotations.Searchable;
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

    public String getId(T object) throws IllegalAccessException {
        String id = null;
        for (Field field : object.getClass().getDeclaredFields()) {

            if (field.getAnnotation(MongoId.class) != null) {
                field.setAccessible(true);

                try {
                    return (String) field.get(object);
                } catch (IllegalAccessException | ClassCastException e) {
                    throw new IllegalStateException(object.getClass().getName() + ": " + e.getMessage());
                }
            }
        }
        if (id == null) {
            for (Field field : object.getClass().getDeclaredFields()) {

                if (field.getName().equals("id") && field.getType().equals(String.class)) {
                    try {
                        return (String) field.get(object);
                    } catch (IllegalAccessException | ClassCastException e) {
                        throw new IllegalStateException(object.getClass().getName() + ": " + e.getMessage());
                    }
                }
            }
        }
        throw new IllegalStateException(object.getClass().getName() + ": has no annotation id. You must annotate the class with MongoId and if no id generator is specified, you must generate your own.");


    }


    @Override
    public List<T> search(SearchSetup query) {

        final List<Field> searchableFields = getSearchableFields();
        List<Map<String, String>> expressions = new ArrayList<>();
        Map<String, Object> qmap = new HashMap<>();
        Pattern[] hashs = new Pattern[searchableFields.size()];
        final Pattern pattern = Pattern.compile(".*" + query.getQuery() + ".*", Pattern.CASE_INSENSITIVE);
        for (int i = 0; i < hashs.length; i++) {
            hashs[i] = pattern;
        }
        for (Field field : searchableFields) {
            Map<String, String> keyValue = new HashMap<>();
            keyValue.put(getPropertyName(field), "#");
            expressions.add(keyValue);
        }
        qmap.put("$or", expressions);
        try {

            Find find = query.getQuery() != null ? collection().find(objectMapper.writeValueAsString(qmap).replaceAll("\"#\"", "#"), hashs) : collection().find();
            if (query.getSortBy() != null && query.getSortOption() != null) {
                find = find.sort(String.format("{%s: %d}", query.getSortBy(), query.getSortOption().getVal()));
            }
            return Lists.newArrayList(find.as(classType).iterator());
        } catch (Exception e) {
            e.printStackTrace();
            throw new RestErrorMessage(e.getMessage());
        }

    }

    private String getPropertyName(Field field) {
        if (field.isAnnotationPresent(JsonProperty.class)) {
            return field.getAnnotation(JsonProperty.class).value();
        } else if (field.isAnnotationPresent(MongoId.class)) {
            return "_id";
        } else {
            return field.getName();
        }
    }


    private List<Field> getSearchableFields() {
        List<Field> searchableFields = new ArrayList<>();
        for (Field field : classType.getDeclaredFields()) {
            if (field.isAnnotationPresent(Searchable.class) || field.isAnnotationPresent(MongoId.class)) {
                searchableFields.add(field);
            }
        }
        return searchableFields;
    }


    @Override
    public void delete(String id) {
        collection().remove("{_id: #}", id);
    }

    @Override
    public void update(T item) {
        try {
            collection().update("{_id: #}", getId(item)).with(item);
        } catch (IllegalAccessException e) {
            throw new RestErrorMessage("Problem while finding the id of this class");
        }
    }

    @Override
    public void create(T item) {
        try {
            final Field idField = IdProvider.getIdField(item);
            final RestModel annotation = item.getClass().getAnnotation(RestModel.class);
            if (!annotation.idGenerator().equals(IdGenerationType.NONE)) {
                idField.setAccessible(true);

                idField.set(item, annotation.idGenerator().generateId());
            }

            collection().insert(item);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }

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
