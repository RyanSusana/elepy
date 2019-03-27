package com.elepy.dao.jongo;

import com.elepy.dao.FilterableField;
import com.elepy.models.FieldType;
import com.elepy.models.NumberType;

import java.io.Serializable;

public class MongoFilterTemplate {
    private final String operator;
    private final FilterableField field;
    private final Serializable value;


    public MongoFilterTemplate(String operator, FilterableField field, String value) {
        this.operator = operator;
        this.field = field;


        if (field.getFieldType().equals(FieldType.NUMBER)) {

            NumberType numberType = NumberType.guessType(field.getField());
            if (numberType.equals(NumberType.INTEGER)) {
                this.value = Long.parseLong(value);
            } else {
                this.value = Float.parseFloat(value);
            }
        } else {
            this.value = value;
        }


    }

    public String compile() {
        return String.format("{%s: {%s: #}}", field.getName(), operator);
    }

    public Serializable getValue() {
        return value;
    }
}
