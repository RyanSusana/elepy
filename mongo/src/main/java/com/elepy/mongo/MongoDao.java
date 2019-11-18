package com.elepy.mongo;

import com.elepy.dao.*;
import com.elepy.exceptions.ElepyException;
import com.elepy.mongo.querybuilding.MongoFilterTemplateFactory;
import com.elepy.mongo.querybuilding.MongoFilters;
import com.elepy.mongo.querybuilding.MongoQuery;
import com.elepy.mongo.querybuilding.MongoSearch;
import com.elepy.utils.ReflectionUtils;
import com.google.common.collect.Lists;
import com.mongodb.DB;
import com.mongodb.MongoClient;
import com.mongodb.client.MongoDatabase;
import org.jongo.Find;
import org.jongo.Jongo;
import org.jongo.MongoCollection;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public abstract class MongoDao<T> implements Crud<T> {

    private Jongo jongo;


    public abstract String mongoCollectionName();


    public abstract DB db();


    public MongoClient getMongoClient() {
        return db().getMongoClient();
    }

    public MongoDatabase getDatabase(){
        return getMongoClient().getDatabase(db().getName());
    }

    Jongo getJongo() {
        if (jongo == null) {
            this.jongo = new Jongo(db(), JongoMapperFactory.createMapper());
        }
        return jongo;
    }

    protected MongoCollection collection() {
        return getJongo().getCollection(mongoCollectionName());
    }


    @Override
    public List<T> searchInField(Field field, String qry) {
        final String propertyName = ReflectionUtils.getPropertyName(field);
        return toPage(addDefaultSort(collection().find("{#: #}", propertyName, qry)), new PageSettings(1L, Integer.MAX_VALUE, List.of()), (int) collection().count("{#: #}", propertyName, qry)).getValues();
    }

    private Find addDefaultSort(Find find) {
        final String sort = String.format("{%s: %d}", getModel().getDefaultSortField(), getModel().getDefaultSortDirection().getVal());
        find.sort(sort);
        return find;
    }

    @Override
    public Optional<T> getById(final Serializable id) {
        return Optional.ofNullable(collection().findOne(String.format("{$or: [{_id: #}, {\"%s\": #}]}", getIdFieldProp()), id, id).as(getType()));
    }

    @Override
    public List<T> getAll() {
        return Lists.newArrayList(collection().find().as(getType()).iterator());
    }

    @Override
    public void deleteById(Serializable id) {
        collection().remove(String.format("{$or: [{_id: #}, {\"%s\": #}]}", getIdFieldProp()), id, id);
    }

    @Override
    public void update(T item) {
        final Object id = getId(item);
        collection().update(String.format("{$or: [{_id: #}, {\"%s\": #}]}", getIdFieldProp()), id, id).with(item);

    }

    private String getIdFieldProp() {
        Optional<Field> idProperty = ReflectionUtils.getIdField(getType());
        if (idProperty.isPresent()) {
            return ReflectionUtils.getPropertyName(idProperty.get());
        }
        return "id";
    }


    @Override
    public void create(T item) {
        try {
            collection().save(item);
        } catch (Exception e) {
            throw new ElepyException(e.getMessage(), 500, e);
        }
    }


    @Override
    public Serializable getId(T item) {
        Optional<Serializable> id = ReflectionUtils.getId(item);
        if (!id.isPresent()) {
            throw new ElepyException("No Identifier provided to the object.");
        }
        return id.get();
    }

    private MongoFilters fromQueryFilters(List<Filter> filterQueries) {
        return new MongoFilters(
                filterQueries
                        .stream()
                        .map(MongoFilterTemplateFactory::fromFilter)
                        .collect(Collectors.toList()
                        )
        );
    }

    private Page<T> toPage(Find find, PageSettings pageSearch, int amountOfResultsWithThatQuery) {


        final List<T> values = Lists.newArrayList(find.limit(pageSearch.getPageSize()).skip(((int) pageSearch.getPageNumber() - 1) * pageSearch.getPageSize()).as(getType()).iterator());

        final long remainder = amountOfResultsWithThatQuery % pageSearch.getPageSize();
        long amountOfPages = amountOfResultsWithThatQuery / pageSearch.getPageSize();
        if (remainder > 0) amountOfPages++;


        return new Page<>(pageSearch.getPageNumber(), amountOfPages, values);
    }

    @Override
    public Page<T> search(Query query, PageSettings settings) {
        MongoFilters mongoFilters = fromQueryFilters(query.getFilters());

        MongoSearch mongoSearch = new MongoSearch(query.getSearchQuery(), getType());

        MongoQuery mongoQuery = new MongoQuery(mongoSearch, mongoFilters);

        String sort = settings.getPropertySortList()
                .stream()
                .map(propertySort -> String.format("'%s': %d", propertySort.getProperty(), propertySort.getSortOption().getVal()))
                .collect(Collectors.joining(","));

        final ArrayList<T> values = Lists.newArrayList(collection()
                .find(mongoQuery.compile(), (Object[]) mongoQuery.getParameters())
                .limit(settings.getPageSize())
                .skip((int) ((settings.getPageNumber() - 1) * settings.getPageSize()))
                .sort(String.format("{%s}", sort))
                .as(getType())
                .iterator());


        long amountOfResultsWithThatQuery = count(mongoQuery);
        final long remainder = amountOfResultsWithThatQuery % settings.getPageSize();
        long amountOfPages = amountOfResultsWithThatQuery / settings.getPageSize();
        if (remainder > 0) amountOfPages++;

        return new Page<>(settings.getPageNumber(), amountOfPages, values);
    }

    private long count(MongoQuery query) {
        return collection().count(query.compile(), (Object[]) query.getParameters());
    }
}
