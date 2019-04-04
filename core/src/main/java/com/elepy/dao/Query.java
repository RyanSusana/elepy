package com.elepy.dao;

import java.util.List;

public class Query {
    private final String searchQuery;
    private final List<FilterQuery> filterQueries;

    public Query(String searchQuery, List<FilterQuery> filterQueries) {
        this.searchQuery = searchQuery;
        this.filterQueries = filterQueries;
    }

    public String getSearchQuery() {
        return searchQuery;
    }

    public List<FilterQuery> getFilterQueries() {
        return filterQueries;
    }
}
