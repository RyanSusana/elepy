package com.elepy.query;

import java.util.LinkedHashMap;
import java.util.Map;

public class SortingSpecification {

    private final Map<String, SortOption> specificationMap = new LinkedHashMap<>();

    public SortingSpecification() {
    }

    public Map<String, SortOption> getMap() {
        return specificationMap;
    }


    public void add(String propName, SortOption option) {
        specificationMap.put(propName, option);
    }
}
