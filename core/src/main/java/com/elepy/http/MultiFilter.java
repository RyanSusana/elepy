package com.elepy.http;


import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class MultiFilter implements Filter, Iterable<Filter> {

    private final List<Filter> filters = new ArrayList<>();

    @Override
    public void authenticate(HttpContext context) throws Exception {
        for (Filter filter : filters) {
            filter.authenticate(context);
        }
    }

    public boolean add(Filter filter) {
        return filters.add(filter);
    }

    public List<Filter> getFilters() {
        return filters;
    }

    @Override
    public Iterator<Filter> iterator() {
        return filters.iterator();
    }
}
