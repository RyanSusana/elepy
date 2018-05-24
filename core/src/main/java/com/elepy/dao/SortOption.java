package com.elepy.dao;

public enum SortOption {
    ASCENDING(1), DESCENDING(-1);

    private final int val;

    SortOption(int val) {
        this.val = val;
    }

    public int getVal() {
        return val;
    }
}
