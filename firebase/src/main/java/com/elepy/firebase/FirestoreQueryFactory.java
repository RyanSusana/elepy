package com.elepy.firebase;

import com.elepy.dao.Filter;
import com.elepy.exceptions.ElepyException;
import com.google.cloud.firestore.Query;

public class FirestoreQueryFactory {
    public static Query getQuery(Query query, Filter filter) {

        final var name = filter.getFilterableField().getField().getName();
        final var value = filter.getFilterValue();
        switch (filter.getFilterType()) {
            case EQUALS:
                return query.whereEqualTo(name, value);
            case GREATER_THAN:
                return query.whereGreaterThan(name, value);
            case GREATER_THAN_OR_EQUALS:
                return query.whereGreaterThanOrEqualTo(name, value);
            case LESSER_THAN:
                return query.whereLessThan(name, value);
            case LESSER_THAN_OR_EQUALS:
                return query.whereLessThanOrEqualTo(name, value);
            default:
                throw new ElepyException("Firestore does not support the filter: " + filter.getFilterType().getName());
        }

    }
} 
