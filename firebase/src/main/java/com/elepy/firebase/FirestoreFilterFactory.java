package com.elepy.firebase;

import com.elepy.query.BooleanGroup;
import com.elepy.query.Expression;
import com.elepy.query.SearchQuery;
import com.elepy.schemas.Property;
import com.elepy.schemas.Schema;
import com.elepy.utils.DateUtils;
import com.elepy.utils.ReflectionUtils;
import com.google.cloud.firestore.Filter;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Collection;

public class FirestoreFilterFactory<T> {

    private final Schema<T> schema;

    public FirestoreFilterFactory(Schema<T> schema) {
        this.schema = schema;
    }


    public Filter createFilterFromExpression(Expression expression) {
        return switch (expression) {
            case com.elepy.query.Filter filter -> elepyFilter(filter);
            case BooleanGroup booleanGroup -> elepyBooleanGroup(booleanGroup);
            case SearchQuery filter -> elepySearchQuery(filter);
            default -> throw new IllegalStateException("Unexpected value: " + expression);
        };
    }

    private Filter elepySearchQuery(SearchQuery filter) {
        if( filter.getTerm() == null || filter.getTerm().isBlank()) {
            return Filter.and(); // No search term, return an empty filter
        }
        final var equalToFilters = schema.getProperties().stream()
                .filter(Property::isSearchable)
                .map(prop -> startsWith(prop.getName(), filter.getTerm()))
                .toArray(Filter[]::new);

        return Filter.or(equalToFilters);
    }

    private Filter elepyBooleanGroup(BooleanGroup booleanGroup) {
        final var innerFilters = booleanGroup.getExpressions().stream().map(this::createFilterFromExpression).toArray(Filter[]::new);
        return switch (booleanGroup.getOperator()) {
            case AND -> Filter.and(innerFilters);
            case OR -> Filter.or(innerFilters);
        };
    }

    private Filter elepyFilter(com.elepy.query.Filter filter) {
        var property = schema.getProperty(filter.getPropertyName());

        var filterValue = ReflectionUtils.toObject(property.getJavaType(), filter.getFilterValue().toString());

        if (filterValue instanceof BigDecimal) {
            filterValue = ((BigDecimal) filterValue).toPlainString();
        }
        return switch (filter.getFilterType()) {
            case EQUALS -> Filter.equalTo(filter.getPropertyName(), filterValue);
            case NOT_EQUALS -> Filter.notEqualTo(filter.getPropertyName(), filterValue);
            case GREATER_THAN -> Filter.greaterThan(filter.getPropertyName(), filterValue);
            case LESSER_THAN -> Filter.lessThan(filter.getPropertyName(), filterValue);
            case GREATER_THAN_OR_EQUALS -> Filter.greaterThanOrEqualTo(filter.getPropertyName(), filterValue);
            case LESSER_THAN_OR_EQUALS -> Filter.lessThanOrEqualTo(filter.getPropertyName(), filterValue);
            case STARTS_WITH -> startsWith(filter.getPropertyName(), filterValue.toString());
            case CONTAINS -> switch (filterValue) {
                case Collection<?> collection -> Filter.arrayContainsAny(filter.getPropertyName(), collection);
                case String string -> startsWith(filter.getPropertyName(), string);
                default -> throw new IllegalStateException("Unexpected value: " + filterValue);
            };
        };
    }

    private Filter startsWith(String propertyName, String strSearch) {
        int strLength = strSearch.length();
        String strFrontCode = strSearch.substring(0, strLength - 1);
        String strEndCode = strSearch.substring(strLength - 1, strLength); // Or just strSearch.substring(strLength - 1)

        String startCode = strSearch;
        // Get the ASCII strSearch of the last character, increment it, and convert back to char
        String endCode = strFrontCode + (char) (strEndCode.charAt(0) + 1);

        return Filter.and(Filter.greaterThanOrEqualTo(propertyName, startCode), Filter.lessThan(propertyName, endCode));
    }

}
