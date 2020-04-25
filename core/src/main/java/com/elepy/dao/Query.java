package com.elepy.dao;

public class Query {

    private Expression expression;

    private int skip = 0;
    private int limit = Integer.MAX_VALUE;

    private SortingSpecification sortingSpecifications = new SortingSpecification();

    public Query() {

    }

    public Query purge() {
        if (expression.canBeIgnored()) {
            expression = new SearchQuery("");
        } else {
            expression.purge();
        }
        return this;
    }


    public Query(Expression expression) {
        this.expression = expression;
    }

    public Expression getExpression() {
        return expression;
    }

    public void setExpression(Expression expression) {
        this.expression = expression;
    }

    public int getSkip() {
        return skip;
    }

    public Query skip(int skip) {
        this.skip = skip;
        return this;
    }

    public int getLimit() {
        return limit;
    }

    public Query limit(int limit) {
        this.limit = limit;
        return this;
    }

    public SortingSpecification getSortingSpecification() {
        return sortingSpecifications;
    }

    public Query sort(String property, SortOption option) {
        sortingSpecifications.add(property, option);
        return this;
    }

    public Query sort(SortingSpecification sortingSpecification) {
        this.sortingSpecifications = sortingSpecification;
        return this;
    }

    public Query page(int number, int size) {
        limit(size);

        skip((number - 1) * size);

        return this;
    }
}
