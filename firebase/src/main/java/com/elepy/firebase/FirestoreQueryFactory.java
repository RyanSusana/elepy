package com.elepy.firebase;

import com.elepy.dao.Filter;
import com.elepy.exceptions.ElepyException;
import com.elepy.models.FieldType;
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
            case CONTAINS:
                if (filter.getFilterableField().getFieldType().equals(FieldType.ARRAY)) {
                    return query.whereArrayContains(name, value);
                } else {
                    throw new ElepyException("Firestore 'CONTAINS' only works on arrays");
                }
            case IS_NULL:
                return query.whereEqualTo(name, null);
            default:
                throw new ElepyException("Firestore does not support the filter: " + filter.getFilterType().getName());
        }

    }
} 
