package com.elepy.dao;

import com.elepy.exceptions.ElepyException;

public class FilterQuery {
    private final FilterableField filterableField;
    private final FilterType filterType;
    private final String filterValue;

    public FilterQuery(FilterableField filterableField, FilterType filterType, String filterValue) {
        this.filterableField = filterableField;
        this.filterType = filterType;
        this.filterValue = filterValue;

        if (!filterType.canBeUsedBy(filterableField)) {
            throw new ElepyException(String.format("'%s' can't be applied to the field '%s'", filterType.getPrettyName(), filterableField.getName()), 400);
        }
    }


    public FilterableField getFilterableField() {
        return filterableField;
    }

    public FilterType getFilterType() {
        return filterType;
    }

    public String getFilterValue() {
        return filterValue;
    }
}
