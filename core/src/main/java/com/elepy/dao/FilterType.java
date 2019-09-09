package com.elepy.dao;

import com.elepy.models.FieldType;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public enum FilterType {

    EQUALS("Equals", "equals", FieldType.values()),
    NOT_EQUALS("Not Equals", "notEquals", FieldType.values()),
    CONTAINS("Contains", "contains", FieldType.TEXT),

    //Numbers & Dates
    GREATER_THAN("Greater than", "gt", FieldType.NUMBER, FieldType.DATE),
    LESSER_THAN("Lesser than", "lt", FieldType.NUMBER, FieldType.DATE),
    GREATER_THAN_OR_EQUALS("Greater than or equal to", "gte", FieldType.NUMBER, FieldType.DATE),
    LESSER_THAN_OR_EQUALS("Lesser than or equal to", "lte", FieldType.NUMBER, FieldType.DATE),

    ;

    private final String prettyName;
    private final String name;
    private final Set<FieldType> allowedFieldTypes;


    FilterType(String prettyName, String name, FieldType... allowedFieldTypes) {
        this.prettyName = prettyName;
        this.name = name;
        this.allowedFieldTypes = new TreeSet<>(Arrays.asList(allowedFieldTypes));
    }

    public static Optional<FilterType> getByQueryString(String s) {
        return Arrays.stream(FilterType.values()).filter(filterType -> filterType.getName().equals(s)).findFirst();
    }

    public static Set<FilterType> getForFieldType(FieldType fieldType) {
        return Stream.of(values())
                .filter(filterType -> filterType.allowedFieldTypes.contains(fieldType))
                .collect(Collectors.toSet());
    }

    public boolean canBeUsedBy(FilterableField filterableField) {
        return allowedFieldTypes.contains(filterableField.getFieldType());
    }

    public Map<String, String> toMap() {
        return Map.of("prettyName", prettyName, "filter", name);
    }

    public String getPrettyName() {
        return prettyName;
    }

    public String getName() {
        return name;
    }

}
