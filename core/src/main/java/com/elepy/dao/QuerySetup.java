package com.elepy.dao;


import com.elepy.routes.FindManyHandler;

/**
 * This object represents a query to a {@link FindManyHandler}
 */
public class QuerySetup {

    /**
     * The search team. The request parameter is called 'q'
     */
    private final String query;

    /**
     * The field you want to sort the results by
     */
    private final String sortBy;

    /**
     * An ASCENDING or DESCENDING sort
     */
    private final SortOption sortOption;

    /**
     * Which page is requested
     *
     * @see Page
     */
    private final long pageNumber;

    /**
     * The size of a page
     *
     * @see Page
     */
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
