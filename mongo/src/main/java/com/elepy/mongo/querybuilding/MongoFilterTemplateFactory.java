package com.elepy.mongo.querybuilding;

import com.elepy.dao.Filter;
import com.elepy.exceptions.ElepyException;
import com.elepy.models.Schema;

import java.lang.reflect.Field;
import java.util.regex.Pattern;

public class MongoFilterTemplateFactory {


    public static <T> MongoFilterTemplate fromFilter(Filter filter, Schema<T> schema) {

        final var property = schema.getProperty(filter.getPropertyName());
        final Field field;
        try {
            field = schema.getJavaClass().getDeclaredField(property.getJavaName());
        } catch (NoSuchFieldException e) {
            throw new ElepyException("Can't find field", 500, e);
        }

        switch (filter.getFilterType()) {
            case EQUALS:
                return new MongoFilterTemplate("$eq", property, field, filter.getFilterValue());
            case NOT_EQUALS:
                return new MongoFilterTemplate("$ne", property, field, filter.getFilterValue());
            case GREATER_THAN:
                return new MongoFilterTemplate("$gt", property, field, filter.getFilterValue());
            case GREATER_THAN_OR_EQUALS:
                return new MongoFilterTemplate("$gte", property, field, filter.getFilterValue());
            case LESSER_THAN:
                return new MongoFilterTemplate("$lt", property, field, filter.getFilterValue());
            case LESSER_THAN_OR_EQUALS:
                return new MongoFilterTemplate("$lte", property, field, filter.getFilterValue());
            case CONTAINS:
                final Pattern patternContains = Pattern.compile(".*" + filter.getFilterValue() + ".*", Pattern.CASE_INSENSITIVE);
                return new MongoFilterTemplate("$regex", property, field, patternContains.toString());
            case STARTS_WITH:
                final Pattern patternStartsWith = Pattern.compile(filter.getFilterValue() + ".*", Pattern.CASE_INSENSITIVE);
                return new MongoFilterTemplate("$regex", property, field, patternStartsWith.toString());

        }
        throw new ElepyException("Mongo does not support: " + filter.getFilterType().getName());
    }
}
