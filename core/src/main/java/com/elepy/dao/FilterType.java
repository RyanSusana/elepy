package com.elepy.dao;

import com.elepy.models.FieldType;

import java.util.Arrays;
import java.util.Optional;
import java.util.Set;
import java.util.TreeSet;

public enum FilterType {

    EQUALS("Equals", "equals", FieldType.values());

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
