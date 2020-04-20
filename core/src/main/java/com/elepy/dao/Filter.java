package com.elepy.dao;

import com.elepy.exceptions.ElepyException;
import com.elepy.models.Property;

public class Filter {
    private final String filterableField;

    private final FilterType filterType;
    private final String filterValue;

    public Filter(String filterableField, FilterType filterType, String filterValue) {
        this.filterableField = filterableField;
        this.filterType = filterType;
        this.filterValue = filterValue;
    }


    public String getPropertyName() {
        return filterableField;
    }

    public FilterType getFilterType() {
        return filterType;
    }

    public String getFilterValue() {
        return filterValue;
    }
}
