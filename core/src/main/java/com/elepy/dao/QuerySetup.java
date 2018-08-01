package com.elepy.dao;

import javax.annotation.Nullable;


public class QuerySetup {

    private final String query;

    @Nullable
    private final String sortBy;

    @Nullable
    private final SortOption sortOption;

    private final long pageNumber;
    private final int pageSize;

    public QuerySetup(@Nullable String query, @Nullable String sortBy, @Nullable SortOption sortOption, @Nullable Long pageNumber, @Nullable Integer pageSize) {

        this.query = query == null ? "" : query;
        this.pageNumber = pageNumber == null ? 1 : pageNumber;
        this.pageSize = pageSize == null ? Integer.MAX_VALUE : pageSize;

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

    public long getPageNumber() {
        return pageNumber;
    }

    public int getPageSize() {
        return pageSize;
    }
}
