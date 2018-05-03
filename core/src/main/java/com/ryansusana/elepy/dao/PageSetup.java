package com.ryansusana.elepy.dao;

public class PageSetup {

    private final long pageNumber;
    private final int pageSize;

    public PageSetup(int pageSize, long pageNumber) {
        assert pageSize > 0;
        assert pageNumber > 0;
        this.pageNumber = pageNumber;
        this.pageSize = pageSize;
    }


    public long getPageNumber() {
        return pageNumber;
    }

    public int getPageSize() {
        return pageSize;
    }

}
