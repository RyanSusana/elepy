package com.elepy.dao;


import com.elepy.annotations.Identifier;
import com.elepy.annotations.RestModel;
import com.elepy.annotations.Searchable;
import com.elepy.annotations.Unique;
import com.elepy.concepts.IdentityProvider;
import com.elepy.dao.jongo.ElepyMapper;
import com.elepy.exceptions.RestErrorMessage;
import com.elepy.utils.ClassUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.mongodb.DB;
import org.jongo.Find;
import org.jongo.Jongo;
import org.jongo.MongoCollection;
import org.jongo.marshall.jackson.oid.MongoId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;
import java.util.*;
import java.util.regex.Pattern;

public class MongoDao<T> implements Crud<T> {
    private static final Logger LOGGER = LoggerFactory.getLogger(MongoDao.class);
    private final Jongo jongo;
    private final Class<T> classType;
    private final String collectionName;
    private final ObjectMapper objectMapper;
    private final SimpleModule module = new SimpleModule("jongo-custom-module");

    public MongoDao(final DB db, final String collectionName, final Class<T> classType) {
        this(db, collectionName, classType, null);
    }

    public MongoDao(final DB db, final String collectionName, final Class<T> classType, IdentityProvider<T> identityProvider) {


        this.jongo = new Jongo(db, new ElepyMapper(this, identityProvider));


        this.objectMapper = new ObjectMapper();
        this.classType = classType;
        this.collectionName = collectionName.replaceAll("/", "");
    }


    protected MongoCollection collection() {
        return jongo.getCollection(collectionName);
    }


    @Override
    public Page<T> get(PageSetup pageSearch) {
        return toPage(addDefaultSort(collection().find()), pageSearch, (int) collection().count());
    }
    @Override
    public List<T> searchInField(Field field, String qry) {
        final String propertyName = ClassUtils.getPropertyName(field);
        return toPage(addDefaultSort(collection().find("{#, #}", propertyName, qry)), new PageSetup(Integer.MAX_VALUE, 1), (int) collection().count("{#, #}", propertyName, qry)).getValues();
    }

    private Find addDefaultSort(Find find) {
        RestModel restModel = classType.getAnnotation(RestModel.class);
        if (restModel != null) {
            find.sort(String.format("{%s: %d}", restModel.defaultSortField(), restModel.defaultSortDirection().getVal()));
        }
        return find;
    }

    @Override
    public Optional<T> getById(final String id) {
        return Optional.ofNullable(collection().findOne("{$or: [{_id: #}, {id: #}]}", id, id).as(classType));
    }


    @Override
    public long count(String query, Object... parameters) {
        return collection().count(query, parameters);
    }

    @Override
    public Class<T> getType() {
        return classType;
    }


    private Page<T> toPage(Find find, PageSetup pageSearch, int amountOfResultsWithThatQuery) {


        final List<T> values = Lists.newArrayList(find.limit(pageSearch.getPageSize()).skip(((int) pageSearch.getPageNumber() - 1) * pageSearch.getPageSize()).as(classType).iterator());

        final long remainder = amountOfResultsWithThatQuery % pageSearch.getPageSize();
        long amountOfPages = amountOfResultsWithThatQuery / pageSearch.getPageSize();
        if (remainder > 0) amountOfPages++;
        return new Page<T>(pageSearch.getPageNumber(), amountOfPages, values);
    }

    @Override
    public Page<T> search(SearchSetup query, PageSetup pageSetup) {

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
            keyValue.put(ClassUtils.getPropertyName(field), "#");
            expressions.add(keyValue);
        }
        qmap.put("$or", expressions);
        try {

            Find find = query.getQuery() != null ? collection().find(objectMapper.writeValueAsString(qmap).replaceAll("\"#\"", "#"), (Object[]) hashs) : collection().find();

            long amountResultsTotal = query.getQuery() != null ? collection().count(objectMapper.writeValueAsString(qmap).replaceAll("\"#\"", "#"), (Object[]) hashs) : collection().count();
            if (query.getSortBy() != null && query.getSortOption() != null) {
                find.sort(String.format("{%s: %d}", query.getSortBy(), query.getSortOption().getVal()));
            } else {
                addDefaultSort(find);
            }
            return toPage(find, pageSetup, (int) amountResultsTotal);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RestErrorMessage(e.getMessage());
        }

    }




    private List<Field> getSearchableFields() {
        return ClassUtils.searchForFieldsWithAnnotation(classType, Identifier.class, Searchable.class, MongoId.class, Unique.class);
    }


    @Override
    public void delete(String id) {
        collection().remove("{$or: [{_id: #}, {id: #}]}", id, id);
    }

    @Override
    public void deleteQuery(String pattern, Object... params) {
        collection().remove(pattern, params);
    }

    @Override
    public void update(T item) {
        final String id = getId(item);
        collection().update("{$or: [{_id: #}, {id: #}]}", id, id).with(item);

    }

    @Override
    public void create(Iterable<T> items) {
        try {


            final T[] ts = Iterables.toArray(items, getType());


            collection().insert((Object[]) ts);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RestErrorMessage(e.getMessage());
        }
    }

    @Override
    public void create(T item) {
        try {
            collection().insert(item);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RestErrorMessage(e.getMessage());
        }
    }


    @Override
    public String getId(T item) {
        Optional<String> id = ClassUtils.getId(item);
        if (!id.isPresent()) {
            throw new IllegalStateException(item.getClass().getName() + ": has no annotation id. You must annotate the class with MongoId and if no id generator is specified, you must generate your own.");
        }
        return id.get();
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
