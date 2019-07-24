package com.elepy.evaluators;

import com.elepy.describers.Property;
import com.elepy.describers.props.DatePropertyConfig;
import com.elepy.describers.props.FileReferencePropertyConfig;
import com.elepy.describers.props.NumberPropertyConfig;
import com.elepy.describers.props.TextPropertyConfig;
import com.elepy.exceptions.ElepyException;
import com.elepy.models.FieldType;
import com.elepy.utils.ModelUtils;

import java.lang.reflect.Field;
import java.util.Date;

public class DefaultObjectEvaluator<T> implements ObjectEvaluator<T> {
    public void evaluate(Object o) throws Exception {
        Class c = o.getClass();

        evaluateObject(o, c);
    }

    public void evaluateObject(Object o, Class c) throws Exception {
        for (Field field : c.getDeclaredFields()) {
            field.setAccessible(true);
            var fieldDescriber = ModelUtils.describeFieldOrMethod(field);

            if (fieldDescriber.getType().equals(FieldType.OBJECT)) {
                if (field.get(o) != null)
                    evaluateObject(field.get(o), field.getType());
            } else {
                checkProperty(field.get(o), fieldDescriber);
            }
        }
    }

    private void checkProperty(Object obj, Property property) {
        checkRequired(obj, property);

        if (property.getType().equals(FieldType.NUMBER)) {
            obj = checkNumber(obj, property);

        }
        if (property.getType().equals(FieldType.TEXT)) {
            checkText((String) obj, property);

        }
        if (property.getType().equals(FieldType.DATE)) {
            checkDate(obj, property);
        }

        if (property.getType().equals(FieldType.FILE_REFERENCE)) {
            checkFileReference(obj, property);
        }

    }

    private void checkRequired(Object obj, Property property) {
        if (property.isRequired() && (obj == null || (obj instanceof Date && ((Date) obj).getTime() < 1000) || (obj instanceof String && ((String) obj).isEmpty()))) {
            throw new ElepyException(property.getPrettyName() + " is blank, please fill it in!");
        }
    }

    private void checkFileReference(Object obj, Property property) {

        if (obj != null && !(obj instanceof String)) {
            throw new ElepyException(String.format("%s must be a String", property.getPrettyName()));
        }
        final var config = FileReferencePropertyConfig.of(property);


        //TODO check file extensions
    }

    private Object checkNumber(Object obj, Property property) {
        if (obj == null) {
            obj = 0;
        }
        if (!(obj instanceof Number)) {
            throw new ElepyException(property.getPrettyName() + " must be a number");
        }
        Number number = (Number) obj;


        NumberPropertyConfig numberAnnotation = NumberPropertyConfig.of(property);
        if (number.floatValue() > numberAnnotation.getMaximum() || number.floatValue() < numberAnnotation.getMinimum()) {
            throw new ElepyException(String.format("%s must be between %d and %d", property.getPrettyName(), (int) numberAnnotation.getMinimum(), (int) numberAnnotation.getMaximum()));
        }
        return obj;
    }

    private void checkText(String obj, Property property) {
        String text = obj;
        if (text == null) {
            text = "";
        }
        TextPropertyConfig textAnnotation = TextPropertyConfig.of(property);
        if (text.length() > textAnnotation.getMaximumLength() || text.length() < textAnnotation.getMinimumLength()) {
            throw new ElepyException(String.format("%s must be between %d and %d characters long", property.getPrettyName(), textAnnotation.getMinimumLength(), textAnnotation.getMaximumLength()));
        }
    }

    private void checkDate(Object obj, Property property) {
        Date date = obj == null ? new Date(0) : (Date) obj;


        DatePropertyConfig dateTimeAnnotation = DatePropertyConfig.of(property);


        Date min = dateTimeAnnotation.getMinimumDate();
        Date max = dateTimeAnnotation.getMaximumDate();
        if (date.before(min) || date.after(max)) {
            throw new ElepyException(String.format("%s must be between '%s' and '%s'", property.getPrettyName(), dateTimeAnnotation.getMinimumDate(), dateTimeAnnotation.getMaximumDate()));

        }
    }
}
