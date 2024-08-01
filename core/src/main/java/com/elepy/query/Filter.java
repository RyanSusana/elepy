package com.elepy.query;

import java.io.Serializable;

// id=5 and amount>5 or
public class Filter extends Expression {
    private String propertyName;

    private FilterType filterType;

    private Serializable filterValue;

    public Filter() {

    }

    public Filter(String propertyName, FilterType filterType, Serializable filterValue) {
        this.propertyName = propertyName;
        this.filterType = filterType;
        this.filterValue = filterValue;
    }


    public void setPropertyName(String propertyName) {
        this.propertyName = propertyName;
    }

    public void setFilterType(FilterType filterType) {
        this.filterType = filterType;
    }

    public void setFilterValue(String filterValue) {
        this.filterValue = filterValue;
    }

    public String getPropertyName() {
        return propertyName;
    }

    public FilterType getFilterType() {
        return filterType;
    }

    public Serializable getFilterValue() {
        return filterValue;
    }

    @Override
    public boolean canBeIgnored() {
        return false;
    }
}
