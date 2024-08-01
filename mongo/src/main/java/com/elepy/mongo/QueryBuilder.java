package com.elepy.mongo;

import com.elepy.query.BooleanGroup;
import com.elepy.query.Expression;
import com.elepy.query.Filter;
import com.elepy.query.SearchQuery;
import com.elepy.exceptions.ElepyException;
import com.elepy.schemas.FieldMapper;
import com.elepy.schemas.Property;
import com.elepy.schemas.Schema;
import com.elepy.utils.ReflectionUtils;
import com.mongodb.client.model.Filters;
import org.bson.conversions.Bson;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.List;
import java.util.stream.Collectors;

import static java.util.regex.Pattern.quote;

public class QueryBuilder<T> {

    private final boolean hasTextIndex;
    private final Schema<T> schema;

    public QueryBuilder(Schema<T> schema) {
        this.schema = schema;
        this.hasTextIndex = false;
    }

    public Bson expression(Expression expression) {
        if (expression instanceof Filter) {
            return filter((Filter) expression);
        } else if (expression instanceof SearchQuery) {
            return search((SearchQuery) expression);
        } else {
            return booleanGroup((BooleanGroup) expression);
        }
    }

    private Bson booleanGroup(BooleanGroup expression) {
        final var groupExpressions = expression.getExpressions().stream().map(this::expression).collect(Collectors.toList());

        if (expression.getOperator().equals(BooleanGroup.BooleanOperator.AND)) {
            return Filters.and(groupExpressions);
        } else {
            return Filters.or(groupExpressions);

        }
    }

    private Bson search(SearchQuery expression) {

        if (hasTextIndex) {
            return textIndexSearch(expression);
        } else {
            return orSearch(expression);
        }
    }

    // TODO add text index searching
    private Bson textIndexSearch(SearchQuery expression) {
        return orSearch(expression);
    }

    private Bson orSearch(SearchQuery expression) {
        return Filters.or(
                getSearchableProperties().stream()
                        .map(property -> Filters.regex(getProp(property.getName()), ".*" + quote(expression.getTerm()) + ".*", "i"))
                        .collect(Collectors.toList())
        );
    }

    private static Serializable value(Field field, Property property, String value) {
        return new FieldMapper().toValueFromString(field, property.getType(), value);
    }

    private Bson filter(Filter filter) {
        final Property property = schema.getProperty(filter.getPropertyName());

        final Field field = ReflectionUtils.getPropertyField(schema.getJavaClass(), property.getName());
        final var propertyName = getProp(filter.getPropertyName());
        final Serializable value = value(field, property, filter.getFilterValue().toString());

        switch (filter.getFilterType()) {
            case EQUALS:
                return Filters.eq(propertyName, value);
            case NOT_EQUALS:
                return Filters.ne(propertyName, value);
            case GREATER_THAN:
                return Filters.gt(propertyName, value);
            case GREATER_THAN_OR_EQUALS:
                return Filters.gte(propertyName, value);
            case LESSER_THAN:
                return Filters.lt(propertyName, value);
            case LESSER_THAN_OR_EQUALS:
                return Filters.lte(propertyName, value);
            case CONTAINS:
                return Filters.regex(propertyName, ".*" + quote(value.toString()) + ".*", "i");
            case STARTS_WITH:
                return Filters.regex(propertyName, quote(value.toString()) + ".*", "i");
            default:
                throw new ElepyException("Mongo does not support: " + filter.getFilterType().getName());
        }
    }

    private String getProp(String prop) {
        final var property = schema.getProperty(prop);
        if (isId(property)) {
            return "_id";
        }
        return property.getName();
    }

    private boolean isId(Property prop) {
        return schema.getIdProperty().equals(prop.getName());
    }

    private List<Property> getSearchableProperties() {
        return schema
                .getProperties().stream().filter(Property::isSearchable).collect(Collectors.toList());
    }
} 
