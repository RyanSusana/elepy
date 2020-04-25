package com.elepy.dao;

import java.io.Serializable;
import java.util.List;

public class Filters {
    public static Filter eq(String propertyName, Serializable value) {
        return filter(propertyName, FilterType.EQUALS, value);
    }

    public static Filter ne(String propertyName, Serializable value) {
        return filter(propertyName, FilterType.NOT_EQUALS, value);
    }

    public static Filter contains(String propertyName, Serializable value) {
        return filter(propertyName, FilterType.CONTAINS, value);
    }

    public static Filter lte(String propertyName, Number value) {
        return filter(propertyName, FilterType.LESSER_THAN_OR_EQUALS, value);
    }

    public static Filter lt(String propertyName, Number value) {
        return filter(propertyName, FilterType.LESSER_THAN, value);
    }

    public static Filter gte(String propertyName, Number value) {
        return filter(propertyName, FilterType.GREATER_THAN_OR_EQUALS, value);
    }

    public static Filter gt(String propertyName, Number value) {
        return filter(propertyName, FilterType.GREATER_THAN, value);
    }

    public static Filter startsWith(String propertyName, Number value) {
        return filter(propertyName, FilterType.STARTS_WITH, value);
    }

    public static Filter filter(String propertyName, FilterType type, Serializable value) {
        return new Filter(propertyName, type, value);
    }

    public static SearchQuery search(String s) {
        return new SearchQuery(s);
    }

    public static BooleanGroup and(Expression... expressions) {
        return booleanGroup(BooleanGroup.BooleanOperator.AND, expressions);
    }

    public static BooleanGroup and(List<? extends Expression> expressions) {
        return booleanGroup(BooleanGroup.BooleanOperator.AND, expressions);
    }

    public static BooleanGroup or(Expression... expressions) {
        return booleanGroup(BooleanGroup.BooleanOperator.OR, expressions);
    }

    public static BooleanGroup or(List<? extends Expression> expressions) {
        return booleanGroup(BooleanGroup.BooleanOperator.OR, expressions);
    }

    public static BooleanGroup booleanGroup(BooleanGroup.BooleanOperator operator, Expression... expressions) {
        return booleanGroup(operator, List.of(expressions));
    }

    public static BooleanGroup booleanGroup(BooleanGroup.BooleanOperator operator, List<? extends Expression> expressions) {
        final var predicateGroup = new BooleanGroup();

        predicateGroup.setOperator(operator);
        predicateGroup.setExpressions(List.copyOf(expressions));
        return predicateGroup;
    }
} 
