package com.elepy.dao.jongo;

import com.elepy.dao.FilterQuery;
import com.elepy.exceptions.ElepyException;

public class MongoFilterTemplateFactory {

    public static MongoFilterTemplate fromFilter(FilterQuery filterQuery) {

        switch (filterQuery.getFilterType()) {
            case EQUALS:
                return new MongoFilterTemplate("$eq", filterQuery.getFilterableField(), filterQuery.getFilterValue());
        }
        throw new ElepyException("Mongo does not support: " + filterQuery.getFilterType().getQueryName());
    }

}
