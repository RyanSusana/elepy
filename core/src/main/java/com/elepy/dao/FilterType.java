package com.elepy.dao;

import com.elepy.models.FieldType;
import com.elepy.models.Property;

import java.util.Arrays;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.elepy.models.FieldType.*;

public enum FilterType {

    EQUALS("Equals", "equals", Set.of("=", "eq", "==", ":"), FieldType.values()),

    NOT_EQUALS("Not Equals", "notEquals", Set.of("!=", "<>", "ne", "neq", "not equal to", "not equals", "not equals to"), FieldType.values()),
    CONTAINS("Contains", "contains", Set.of("in"),
            INPUT,
            FILE_REFERENCE,
            TEXTAREA,
            MARKDOWN,
            HTML,
            ARRAY),

    //Numbers & Dates
    GREATER_THAN("Greater than", "gt", Set.of(">", "gt"), NUMBER, DATE),
    LESSER_THAN("Lesser than", "lt", Set.of("<", "lt"), NUMBER, DATE),
    GREATER_THAN_OR_EQUALS("Greater than or equal to", "gte", Set.of(">="), NUMBER, DATE),
    LESSER_THAN_OR_EQUALS("Lesser than or equal to", "lte", Set.of("<="), NUMBER, DATE),

    //Strings
    STARTS_WITH("Starts with", "startsWith", Set.of("sw", "starts with"),
            INPUT,
            FILE_REFERENCE,
            TEXTAREA,
            MARKDOWN,
            HTML,
            ARRAY);


    private final String prettyName;
    private final String name;
    private final Set<String> synonyms;
    private final Set<FieldType> allowedFieldTypes;


    FilterType(String prettyName, String name, FieldType... allowedFieldTypes) {
        this(prettyName, name, Set.of(), allowedFieldTypes);
    }

    FilterType(String prettyName, String name, Set<String> synonyms, FieldType... allowedFieldTypes) {
        this.prettyName = prettyName;
        this.name = name;
        this.synonyms = synonyms;
        this.allowedFieldTypes = Set.of(allowedFieldTypes);
    }

    public static Optional<FilterType> getByQueryString(String s) {
        return Arrays.stream(FilterType.values()).filter(filterType -> filterType.getName().equals(s)).findFirst();
    }

    public static Set<FilterType> getForFieldType(FieldType fieldType) {
        return Stream.of(values())
                .filter(filterType -> filterType.allowedFieldTypes.contains(fieldType))
                .collect(Collectors.toSet());
    }

    public boolean canBeUsedBy(Property filterableField) {
        return allowedFieldTypes.contains(filterableField.getType());
    }

    public Map<String, Object> toMap() {
        return Map.of("prettyName", prettyName, "filter", name, "synonyms", synonyms);
    }

    public String getPrettyName() {
        return prettyName;
    }

    public String getName() {
        return name;
    }

}
