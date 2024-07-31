package com.elepy.dao.querymodel;

public enum SortOption {
    ASCENDING(1), DESCENDING(-1);

    private final int val;

    SortOption(int val) {
        this.val = val;
    }

    public static SortOption get(String s) {
        if (s.toLowerCase().contains("des")) {
            return DESCENDING;
        }
        return ASCENDING;
    }

    public int getVal() {
        return val;
    }
}
