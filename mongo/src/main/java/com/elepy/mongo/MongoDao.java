package com.elepy.mongo;

import com.elepy.annotations.RestModel;
import com.elepy.dao.*;
import com.elepy.exceptions.ElepyException;
import com.elepy.utils.ReflectionUtils;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.mongodb.DB;
import org.jongo.Find;
import org.jongo.Jongo;
import org.jongo.MongoCollection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import spark.utils.StringUtils;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public abstract class MongoDao<T> implements Crud<T> {

    private static final Logger logger = LoggerFactory.getLogger(DefaultMongoDao.class);

    private Jongo jongo;

    public abstract Class<T> modelType();

    public abstract String mongoCollectionName();


    public abstract DB db();


    Jongo getJongo() {
        if (jongo == null) {
            this.jongo = new Jongo(db(), new ElepyMapper(this));
        }
        return jongo;
    }

    protected MongoCollection collection() {
        return getJongo().getCollection(mongoCollectionName());
    }


    @Override
    public List<T> searchInField(Field field, String qry) {
        final String propertyName = ReflectionUtils.getPropertyName(field);
        return toPage(addDefaultSort(collection().find("{#: #}", propertyName, qry)), new SearchQuery(null, null, null, 1L, Integer.MAX_VALUE), (int) collection().count("{#: #}", propertyName, qry)).getValues();
    }

    private Find addDefaultSort(Find find) {
        RestModel restModel = modelType().getAnnotation(RestModel.class);
        if (restModel != null) {
            find.sort(String.format("{%s: %d}", restModel.defaultSortField(), restModel.defaultSortDirection().getVal()));
        }
        return find;
    }

    @Override
    public Optional<T> getById(final Serializable id) {
        return Optional.ofNullable(collection().findOne(String.format("{$or: [{_id: #}, {\"%s\": #}]}", getIdFieldProp()), id, id).as(modelType()));
    }

    @Override
    public List<T> getAll() {
        return Lists.newArrayList(collection().find().as(modelType()).iterator());
    }

    @Override
    public long count(String query) {
        if (StringUtils.isEmpty(query)) {
            return collection().count();
        }
        if (query.startsWith("{") && query.endsWith("}")) {
            return collection().count(query);
        } else {
            String queryCompiled = new MongoSearch(query, modelType()).compile();

            return collection().count(queryCompiled);
        }
    }

    @Override
    public Class<T> getType() {
        return modelType();
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
        Optional<Field> idField = ReflectionUtils.getIdField(modelType());
        if (idField.isPresent()) {
            return ReflectionUtils.getPropertyName(idField.get());
        }
        return "id";
    }

    @Override
    public void create(Iterable<T> items) {
        try {
            final T[] ts = Iterables.toArray(items, getType());
            collection().insert((Object[]) ts);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            throw new ElepyException(e.getMessage());
        }
    }

    @Override
    public void create(T item) {
        try {
            collection().insert(item);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            throw new ElepyException(e.getMessage());
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

    private MongoFilters fromQueryFilters(List<FilterQuery> filterQueries) {
        return new MongoFilters(
                filterQueries
                        .stream()
                        .map(MongoFilterTemplateFactory::fromFilter)
                        .collect(Collectors.toList()
                        )
        );
    }

    private Page<T> toPage(Find find, SearchQuery pageSearch, int amountOfResultsWithThatQuery) {


        final List<T> values = Lists.newArrayList(find.limit(pageSearch.getPageSize()).skip(((int) pageSearch.getPageNumber() - 1) * pageSearch.getPageSize()).as(modelType()).iterator());

        final long remainder = amountOfResultsWithThatQuery % pageSearch.getPageSize();
        long amountOfPages = amountOfResultsWithThatQuery / pageSearch.getPageSize();
        if (remainder > 0) amountOfPages++;


        return new Page<>(pageSearch.getPageNumber(), amountOfPages, values);
    }

    @Override
    public Page<T> search(Query query, PageSettings settings) {
        MongoFilters mongoFilters = fromQueryFilters(query.getFilterQueries());

        MongoSearch mongoSearch = new MongoSearch(query.getSearchQuery(), modelType());

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
                .as(modelType())
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
