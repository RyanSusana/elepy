package com.elepy.mongo;

import com.elepy.dao.FilterableField;
import com.elepy.utils.MapperUtils;

import java.io.Serializable;

public class MongoFilterTemplate {
    private final String operator;
    private final FilterableField field;
    private final Serializable value;


    public MongoFilterTemplate(String operator, FilterableField field, String value) {
        this.operator = operator;
        this.field = field;
        this.value = MapperUtils.toValueFromString(field.getField(), field.getFieldType(), value);
    }


    public String compile() {
        return String.format("{%s: {%s: #}}", field.getName(), operator);
    }

    public Serializable getValue() {
        return value;
    }
}
