package com.elepy.dao;


public class SearchSetup {
    private final String query;
    private final String sortBy;
    private final SortOption sortOption;

    public SearchSetup(String query, String sortBy, SortOption sortOption) {
        this.query = query;
        this.sortBy = sortBy;
        this.sortOption = sortOption;

    }

    public String getQuery() {
        return query;
    }

    public String getSortBy() {
        return sortBy;
    }

    public SortOption getSortOption() {
        return sortOption;
    }
}
