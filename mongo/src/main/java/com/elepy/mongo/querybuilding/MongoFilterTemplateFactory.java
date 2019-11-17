package com.elepy.mongo.querybuilding;

import com.elepy.dao.Filter;
import com.elepy.exceptions.ElepyException;

import java.util.regex.Pattern;

public class MongoFilterTemplateFactory {

    public static MongoFilterTemplate fromFilter(Filter filter) {

        switch (filter.getFilterType()) {
            case EQUALS:
                return new MongoFilterTemplate("$eq", filter.getFilterableField(), filter.getFilterValue());
            case NOT_EQUALS:
                return new MongoFilterTemplate("$ne", filter.getFilterableField(), filter.getFilterValue());
            case GREATER_THAN:
                return new MongoFilterTemplate("$gt", filter.getFilterableField(), filter.getFilterValue());
            case GREATER_THAN_OR_EQUALS:
                return new MongoFilterTemplate("$gte", filter.getFilterableField(), filter.getFilterValue());
            case LESSER_THAN:
                return new MongoFilterTemplate("$lt", filter.getFilterableField(), filter.getFilterValue());
            case LESSER_THAN_OR_EQUALS:
                return new MongoFilterTemplate("$lte", filter.getFilterableField(), filter.getFilterValue());
            case CONTAINS:
                final Pattern patternContains = Pattern.compile(".*" + filter.getFilterValue() + ".*", Pattern.CASE_INSENSITIVE);
                return new MongoFilterTemplate("$regex", filter.getFilterableField(), patternContains.toString());
            case STARTS_WITH:
                final Pattern patternStartsWith = Pattern.compile( filter.getFilterValue() + ".*", Pattern.CASE_INSENSITIVE);
                return new MongoFilterTemplate("$regex", filter.getFilterableField(), patternStartsWith.toString());

        }
        throw new ElepyException("Mongo does not support: " + filter.getFilterType().getName());
    }

}
