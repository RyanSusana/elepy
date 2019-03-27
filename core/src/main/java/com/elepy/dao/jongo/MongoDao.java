package com.elepy.dao.jongo;

import com.elepy.annotations.Identifier;
import com.elepy.annotations.RestModel;
import com.elepy.annotations.Searchable;
import com.elepy.annotations.Unique;
import com.elepy.dao.*;
import com.elepy.exceptions.ElepyException;
import com.elepy.utils.ClassUtils;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.mongodb.DB;
import org.jongo.Find;
import org.jongo.Jongo;
import org.jongo.MongoCollection;
import org.jongo.marshall.jackson.oid.MongoId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import spark.utils.StringUtils;

import java.lang.reflect.Field;
import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public abstract class MongoDao<T> implements Crud<T> {

    private static final Logger logger = LoggerFactory.getLogger(DefaultMongoDao.class);

    private Jongo jongo;

    public abstract Class<T> modelClassType();

    public abstract String mongoCollectionName();

    public abstract ObjectMapper objectMapper();

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
        final String propertyName = ClassUtils.getPropertyName(field);
        return toPage(addDefaultSort(collection().find("{#: #}", propertyName, qry)), new SearchQuery(null, null, null, 1L, Integer.MAX_VALUE), (int) collection().count("{#: #}", propertyName, qry)).getValues();
    }

    private Find addDefaultSort(Find find) {
        RestModel restModel = modelClassType().getAnnotation(RestModel.class);
        if (restModel != null) {
            find.sort(String.format("{%s: %d}", restModel.defaultSortField(), restModel.defaultSortDirection().getVal()));
        }
        return find;
    }

    @Override
    public Optional<T> getById(final Object id) {
        return Optional.ofNullable(collection().findOne(String.format("{$or: [{_id: #}, {\"%s\": #}]}", getIdFieldProp()), id, id).as(modelClassType()));
    }


    @Override
    public long count(String query) {
        if (StringUtils.isEmpty(query)) {
            return collection().count();
        }
        if (query.startsWith("{") && query.endsWith("}")) {
            return collection().count(query);
        } else {
            final List<Field> searchableFields = getSearchableFields();

            List<Map<String, String>> expressions = new ArrayList<>();
            Map<String, Object> qmap = new HashMap<>();


            Pattern[] patterns = new Pattern[searchableFields.size()];

            final Pattern pattern = Pattern.compile(query, Pattern.CASE_INSENSITIVE);
            for (int i = 0; i < patterns.length; i++) {
                patterns[i] = pattern;
            }
            for (Field field : searchableFields) {
                Map<String, String> keyValue = new HashMap<>();
                keyValue.put(ClassUtils.getPropertyName(field), "#");
                expressions.add(keyValue);
            }
            qmap.put("$or", expressions);

            try {
                return collection().count(objectMapper().writeValueAsString(qmap).replaceAll("\"#\"", "#"), (Object[]) patterns);
            } catch (JsonProcessingException e) {
                logger.error(e.getMessage(), e);
                throw new ElepyException(e.getMessage());
            }
        }

    }

    @Override
    public Class<T> getType() {
        return modelClassType();
    }


    public Page<T> search(SearchQuery searchQuery) {
        final Find find;
        final long amountResultsTotal;
        try {
            if (!StringUtils.isEmpty(searchQuery.getQuery())) {
                final List<Field> searchableFields = getSearchableFields();

                List<Map<String, String>> expressions = new ArrayList<>();
                Map<String, Object> qmap = new HashMap<>();


                Pattern[] patterns = new Pattern[searchableFields.size()];

                final Pattern pattern = Pattern.compile(".*" + searchQuery.getQuery() + ".*", Pattern.CASE_INSENSITIVE);
                for (int i = 0; i < patterns.length; i++) {
                    patterns[i] = pattern;
                }
                for (Field field : searchableFields) {
                    Map<String, String> keyValue = new HashMap<>();
                    keyValue.put(ClassUtils.getPropertyName(field), "#");
                    expressions.add(keyValue);
                }
                qmap.put("$or", expressions);
                find = searchQuery.getQuery() != null ? collection().find(objectMapper().writeValueAsString(qmap).replaceAll("\"#\"", "#"), (Object[]) patterns) : collection().find();

                amountResultsTotal = collection().count(objectMapper().writeValueAsString(qmap).replaceAll("\"#\"", "#"), (Object[]) patterns);
            } else {
                find = collection().find();
                amountResultsTotal = collection().count();
            }

            final AbstractMap.SimpleEntry<String, SortOption> defaultSort = defaultSort();

            find.sort(String.format("{%s: %d}",
                    searchQuery.getSortBy() == null ? defaultSort.getKey() : searchQuery.getSortBy(),
                    searchQuery.getSortOption() == null ? defaultSort.getValue().getVal() : searchQuery.getSortOption().getVal()));

            return toPage(find, searchQuery, (int) amountResultsTotal);
        } catch (JsonProcessingException e) {
            logger.error(e.getMessage(), e);
            throw new ElepyException(e.getMessage());
        }

    }


    private List<Field> getSearchableFields() {
        return ClassUtils.searchForFieldsWithAnnotation(modelClassType(), Identifier.class, Searchable.class, MongoId.class, Unique.class);
    }


    @Override
    public void delete(Object id) {
        collection().remove(String.format("{$or: [{_id: #}, {\"%s\": #}]}", getIdFieldProp()), id, id);
    }

    @Override
    public void update(T item) {
        final Object id = getId(item);
        collection().update(String.format("{$or: [{_id: #}, {\"%s\": #}]}", getIdFieldProp()), id, id).with(item);

    }

    private String getIdFieldProp() {
        Optional<Field> idField = ClassUtils.getIdField(modelClassType());
        if (idField.isPresent()) {
            return ClassUtils.getPropertyName(idField.get());
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

    public long count(List<FilterQuery> filterQueries) {
        MongoFilters mongoFilters = fromQueryFilters(filterQueries);

        return count(mongoFilters);

    }


    public Page<T> filter(int pageNumber, int pageSize, List<FilterQuery> filterQueries) {

        if (filterQueries.size() == 0) {
            return search(new SearchQuery("", null, null, (long) pageSize, pageSize));
        }
        MongoFilters mongoFilters = fromQueryFilters(filterQueries);

        mongoFilters.compile();
        mongoFilters.getHashtagsForJongo();
        ArrayList<T> values = Lists.newArrayList(collection().find(mongoFilters.compile(), (Object[]) mongoFilters.getHashtagsForJongo())
                .limit(pageSize)
                .skip((pageNumber - 1) * pageSize)
                .as(modelClassType())
                .iterator());

        long amountOfResultsWithThatQuery = count(mongoFilters);
        final long remainder = amountOfResultsWithThatQuery % pageSize;
        long amountOfPages = amountOfResultsWithThatQuery / pageSize;
        if (remainder > 0) amountOfPages++;

        return new Page<>(pageNumber, amountOfPages, values);
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
    public Object getId(T item) {
        Optional<Object> id = ClassUtils.getId(item);
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


        final List<T> values = Lists.newArrayList(find.limit(pageSearch.getPageSize()).skip(((int) pageSearch.getPageNumber() - 1) * pageSearch.getPageSize()).as(modelClassType()).iterator());

        final long remainder = amountOfResultsWithThatQuery % pageSearch.getPageSize();
        long amountOfPages = amountOfResultsWithThatQuery / pageSearch.getPageSize();
        if (remainder > 0) amountOfPages++;


        return new Page<>(pageSearch.getPageNumber(), amountOfPages, values);
    }

    private long count(MongoFilters mongoFilters) {
        return collection().count(mongoFilters.compile(), (Object[]) mongoFilters.getHashtagsForJongo());
    }
}
