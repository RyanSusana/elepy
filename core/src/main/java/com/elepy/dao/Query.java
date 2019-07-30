package com.elepy.dao;

import java.util.ArrayList;
import java.util.List;

public class Query {
    private final String searchQuery;
    private final List<Filter> filters;

    public Query(String searchQuery, List<Filter> filters) {
        this.searchQuery = searchQuery;
        this.filters = filters;
    }

    public String getSearchQuery() {
        return searchQuery;
    }

    public static QueryBuilder builder() {
        return QueryBuilder.aQuery();
    }

    public List<Filter> getFilters() {
        return filters;
    }

    public static final class QueryBuilder {
        private String searchQuery;
        private List<Filter> filterQueries;

        private QueryBuilder() {
            this.filters(new ArrayList<>());
        }

        public static QueryBuilder aQuery() {
            return new QueryBuilder();
        }

        public QueryBuilder query(String searchQuery) {
            this.searchQuery = searchQuery;
            return this;
        }

        public QueryBuilder filters(List<Filter> filterQueries) {
            this.filterQueries = filterQueries;
            return this;
        }

        public QueryBuilder filter(Filter filter) {
            this.filterQueries.add(filter);
            return this;
        }

        public Query build() {
            return new Query(searchQuery, filterQueries);
        }
    }
}
