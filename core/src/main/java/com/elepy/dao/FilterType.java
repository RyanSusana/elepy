package com.elepy.dao;

import com.elepy.models.FieldType;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.elepy.models.FieldType.*;

public enum FilterType {

    EQUALS("Equals", "equals", FieldType.values()),
    NOT_NULL("Has Value", "notNull", FieldType.values()),
    IS_NULL("Doesn't have value", "isNull", FieldType.values()),
    NOT_EQUALS("Not Equals", "notEquals", FieldType.values()),
    CONTAINS("Contains", "contains",
            INPUT,
            FILE_REFERENCE,
            TEXTAREA,
            MARKDOWN,
            HTML,
            ARRAY),

    //Numbers & Dates
    GREATER_THAN("Greater than", "gt", NUMBER, DATE),
    LESSER_THAN("Lesser than", "lt", NUMBER, DATE),
    GREATER_THAN_OR_EQUALS("Greater than or equal to", "gte", NUMBER, DATE),
    LESSER_THAN_OR_EQUALS("Lesser than or equal to", "lte", NUMBER, DATE),

    //Strings
    STARTS_WITH("Starts with", "startsWith",
            INPUT,
            FILE_REFERENCE,
            TEXTAREA,
            MARKDOWN,
            HTML,
            ARRAY);


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
