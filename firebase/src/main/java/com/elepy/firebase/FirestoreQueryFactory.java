package com.elepy.firebase;

import com.elepy.dao.querymodel.Filter;
import com.elepy.exceptions.ElepyException;
import com.elepy.models.FieldType;
import com.elepy.models.Schema;
import com.google.cloud.firestore.Query;

public class FirestoreQueryFactory {
    public static Query getQuery(Query query, Filter filter, Schema schema) {

        final var propertyName = filter.getPropertyName();

        final var value = filter.getFilterValue();
        final var property = schema.getProperty(propertyName);
        switch (filter.getFilterType()) {
            case EQUALS:
                return query.whereEqualTo(propertyName, value);
            case GREATER_THAN:
                return query.whereGreaterThan(propertyName, value);
            case GREATER_THAN_OR_EQUALS:
                return query.whereGreaterThanOrEqualTo(propertyName, value);
            case LESSER_THAN:
                return query.whereLessThan(propertyName, value);
            case LESSER_THAN_OR_EQUALS:
                return query.whereLessThanOrEqualTo(propertyName, value);
            case CONTAINS:
                if (property.getType().equals(FieldType.ARRAY)) {
                    return query.whereArrayContains(propertyName, value);
                } else {
                    //TODO
                    throw new ElepyException("Firestore 'CONTAINS' only works on arrays");
                }
            default:
                //TODO
                throw new ElepyException("Firestore does not support the filter: " + filter.getFilterType().getName());
        }

    }
} 
