package com.ryansusana.elepy.dao;

import java.util.List;

public class Page<T> {
    private final long currentPageNumber;

    private final long lastPageNumber;

    private final List<T> values;

    private final int originalSize;

    public Page(long currentPageNumber, long lastPageNumber, List<T> values) {
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
