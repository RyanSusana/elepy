package com.elepy.dao.jongo;

import com.elepy.dao.FilterQuery;
import com.elepy.exceptions.ElepyException;

public class MongoFilterTemplateFactory {

    public static MongoFilterTemplate fromFilter(FilterQuery filterQuery) {

        switch (filterQuery.getFilterType()) {
            case EQUALS:
                return new MongoFilterTemplate("$eq", filterQuery.getFilterableField(), filterQuery.getFilterValue());
            case NOT_EQUALS:
                return new MongoFilterTemplate("$ne", filterQuery.getFilterableField(), filterQuery.getFilterValue());
            case GREATER_THAN:
                return new MongoFilterTemplate("$gt", filterQuery.getFilterableField(), filterQuery.getFilterValue());
            case GREATER_THAN_OR_EQUALS:
                return new MongoFilterTemplate("$gte", filterQuery.getFilterableField(), filterQuery.getFilterValue());
            case LESSER_THAN:
                return new MongoFilterTemplate("$lt", filterQuery.getFilterableField(), filterQuery.getFilterValue());
            case LESSER_THAN_OR_EQUALS:
                return new MongoFilterTemplate("$lte", filterQuery.getFilterableField(), filterQuery.getFilterValue());
            case CONTAINS:
                return new MongoFilterTemplate("$regex", filterQuery.getFilterableField(), String.format(".*%s.*", filterQuery.getFilterValue()));
        }
        throw new ElepyException("Mongo does not support: " + filterQuery.getFilterType().getQueryName());
    }

}
