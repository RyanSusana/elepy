package com.elepy.dao;

import java.util.Arrays;
import java.util.Optional;
import java.util.Set;

public enum FilterType {

    EQUALS("Equals", "equals", Set.of("=", "eq", "==", ":")),

    NOT_EQUALS("Not Equals", "notEquals", Set.of("!=", "<>", "ne", "neq", "not equal to", "not equals", "not equals to")),
    CONTAINS("Contains", "contains", Set.of("in")
    ),

    //Numbers & Dates
    GREATER_THAN("Greater than", "gt", Set.of(">", "gt")),
    LESSER_THAN("Lesser than", "lt", Set.of("<", "lt")),
    GREATER_THAN_OR_EQUALS("Greater than or equal to", "gte", Set.of(">=")),
    LESSER_THAN_OR_EQUALS("Lesser than or equal to", "lte", Set.of("<=")),

    //Strings
    STARTS_WITH("Starts with", "startsWith", Set.of("sw", "starts with")
    );

    private final String prettyName;
    private final String name;
    private final Set<String> synonyms;


    FilterType(String prettyName, String name, Set<String> synonyms) {
        this.prettyName = prettyName;
        this.name = name;
        this.synonyms = synonyms;
    }

    public static Optional<FilterType> getByQueryString(String s) {
        return Arrays.stream(FilterType.values()).filter(filterType -> filterType.getName().equals(s)).findFirst();
    }

    public FilterTypeDescription toDescription() {
        return new FilterTypeDescription(this, prettyName, name, synonyms);
    }

    public String getPrettyName() {
        return prettyName;
    }

    public String getName() {
        return name;
    }

}
