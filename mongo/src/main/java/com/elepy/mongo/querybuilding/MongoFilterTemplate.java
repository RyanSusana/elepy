package com.elepy.mongo.querybuilding;

import com.elepy.models.Property;
import com.elepy.utils.MapperUtils;

import java.io.Serializable;
import java.lang.reflect.Field;

public class MongoFilterTemplate {
    private final String operator;
    private final Property field;
    private final Serializable value;


    public MongoFilterTemplate(String operator, Property property, Field field, String value) {
        this.operator = operator;
        this.field = property;
        this.value = MapperUtils.toValueFromString(field, property.getType(), value);
    }


    public String compile() {
        return String.format("{%s: {%s: #}}", field.getName(), operator);
    }

    public Serializable getValue() {
        return value;
    }
}
