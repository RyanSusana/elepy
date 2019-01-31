package com.elepy.dao;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public class Page<T> {
    /**
     * The number of the found page
     */
    private final long currentPageNumber;

    /**
     * The number of the last page
     */
    private final long lastPageNumber;

    /**
     * The found values. This is effectively the payload of the page, the results.
     */
    private final List<T> values;

    /**
     * This is a property that defines the size of values.
     */
    private final int originalSize;

    @JsonCreator
    public Page(@JsonProperty("currentPageNumber") long currentPageNumber, @JsonProperty("lastPageNumber") long lastPageNumber, @JsonProperty("values") List<T> values) {
        this.currentPageNumber = currentPageNumber;
        this.lastPageNumber = lastPageNumber;
        this.values = values;
        this.originalSize = values.size();
    }

    public long getCurrentPageNumber() {
        return currentPageNumber;
    }

    public long getLastPageNumber() {
        return lastPageNumber;
    }

    public List<T> getValues() {
        return values;
    }

    public int getOriginalSize() {
        return originalSize;
    }
}
