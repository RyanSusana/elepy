package com.elepy.dao;

public class PropertySort {

    private final String property;
    private final SortOption sortOption;

    public PropertySort(String property, SortOption sortOption) {
        this.property = property;
        this.sortOption = sortOption;
    }

    public String getProperty() {
        return property;
    }

    public SortOption getSortOption() {
        return sortOption;
    }
}
