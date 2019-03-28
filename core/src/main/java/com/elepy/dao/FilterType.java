package com.elepy.dao;

import com.elepy.models.FieldType;

import java.util.Arrays;
import java.util.Optional;
import java.util.Set;
import java.util.TreeSet;

public enum FilterType {

    EQUALS("Equals", "equals", FieldType.values()),
    NOT_EQUALS("Not Equals", "notEquals", FieldType.values()),
    CONTAINS("Contains", "contains", FieldType.TEXT),

    //Numbers & Dates
    GREATER_THAN("Greater than", "gt", FieldType.NUMBER, FieldType.DATE),
    LESSER_THAN("Lesser than", "lt", FieldType.NUMBER, FieldType.DATE),
    GREATER_THAN_OR_EQUALS("Greater than or equal to", "gte", FieldType.NUMBER, FieldType.DATE),
    LESSER_THAN_OR_EQUALS("Lesser than or equal to", "lte", FieldType.NUMBER, FieldType.DATE),

    ;

    private final String filterName;
    private final String queryName;
    private final Set<FieldType> fieldTypes;


    FilterType(String filterName, String queryName, FieldType... fieldTypes) {
        this.filterName = filterName;
        this.queryName = queryName;
        this.fieldTypes = new TreeSet<>(Arrays.asList(fieldTypes));
    }

    public static Optional<FilterType> getByQueryString(String s) {
        return Arrays.stream(FilterType.values()).filter(filterType -> filterType.getQueryName().equals(s)).findFirst();
    }

    public boolean canBeUsedBy(FilterableField filterableField) {
        return fieldTypes.contains(filterableField.getFieldType());
    }

    public String getFilterName() {
        return filterName;
    }

    public String getQueryName() {
        return queryName;
    }

}
