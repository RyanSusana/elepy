package com.elepy.dao;


public class QuerySetup {

    private final String query;

    private final String sortBy;

    private final SortOption sortOption;

    private final long pageNumber;
    private final int pageSize;

    public QuerySetup(String query, String sortBy, SortOption sortOption, Long pageNumber, Integer pageSize) {

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
