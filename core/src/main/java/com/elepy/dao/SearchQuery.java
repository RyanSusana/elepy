package com.elepy.dao;

import com.elepy.utils.StringUtils;

public class SearchQuery extends Expression {
    private String term;

    public SearchQuery() {
    }

    public SearchQuery(String term) {
        this.term = term;
    }

    public String getTerm() {
        return term;
    }

    public void setTerm(String term) {
        this.term = term;
    }

    @Override
    public boolean canBeIgnored() {
        return StringUtils.isEmpty(term);
    }
}
