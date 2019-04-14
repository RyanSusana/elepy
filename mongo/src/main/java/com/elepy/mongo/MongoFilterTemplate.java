package com.elepy.mongo;

import com.elepy.annotations.TrueFalse;
import com.elepy.dao.FilterableField;
import com.elepy.exceptions.ElepyException;
import com.elepy.models.FieldType;
import com.elepy.models.NumberType;
import com.elepy.utils.DateUtils;
import com.elepy.utils.ReflectionUtils;

import java.io.Serializable;
import java.util.Date;

public class MongoFilterTemplate {
    private final String operator;
    private final FilterableField field;
    private final Serializable value;


    public MongoFilterTemplate(String operator, FilterableField field, String value) {
        this.operator = operator;
        this.field = field;
        this.value = toRecognizableData(value);
    }

    private Serializable toRecognizableData(String value) {
        try {
            if (field.getFieldType().equals(FieldType.BOOLEAN)) {
                final TrueFalse annotation = field.getField().getAnnotation(TrueFalse.class);

                if (annotation != null && value.equalsIgnoreCase(annotation.trueValue())) {
                    return true;
                }
                return Boolean.parseBoolean(value);
            }
            if (field.getFieldType().equals(FieldType.NUMBER)) {

                NumberType numberType = NumberType.guessType(field.getField());
                if (numberType.equals(NumberType.INTEGER)) {
                    return Long.parseLong(value);
                } else {
                    return Float.parseFloat(value);
                }
            } else if (field.getFieldType().equals(FieldType.DATE)) {

                Date date = DateUtils.guessDate(value);

                if (date == null) {
                    return value;
                } else {
                    return date;
                }
            } else {
                return value;
            }
        } catch (NumberFormatException e) {
            throw new ElepyException(String.format("%s can only be compared to numbers", ReflectionUtils.getPrettyName(field.getField())));
        }
    }

    public String compile() {
        return String.format("{%s: {%s: #}}", field.getName(), operator);
    }

    public Serializable getValue() {
        return value;
    }
}
