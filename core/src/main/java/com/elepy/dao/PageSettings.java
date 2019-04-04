package com.elepy.dao;

import java.util.List;

public class PageSettings {
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

    private final List<PropertySort> propertySortList;

    public PageSettings(long pageNumber, int pageSize, List<PropertySort> propertySortList) {
        this.pageNumber = pageNumber;
        this.pageSize = pageSize;
        this.propertySortList = propertySortList;
    }

    public long getPageNumber() {
        return pageNumber;
    }

    public int getPageSize() {
        return pageSize;
    }

    public List<PropertySort> getPropertySortList() {
        return propertySortList;
    }
}
