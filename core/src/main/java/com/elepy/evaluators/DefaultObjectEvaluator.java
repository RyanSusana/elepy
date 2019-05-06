package com.elepy.evaluators;

import com.elepy.describers.Property;
import com.elepy.describers.StructureDescriber;
import com.elepy.describers.props.DatePropertyConfig;
import com.elepy.describers.props.NumberPropertyConfig;
import com.elepy.describers.props.TextPropertyConfig;
import com.elepy.exceptions.ElepyException;
import com.elepy.models.FieldType;

import java.lang.reflect.Field;
import java.util.Date;

public class DefaultObjectEvaluator<T> implements ObjectEvaluator<T> {


    public void evaluate(T o, Class<T> clazz) throws Exception {

        Class c = o.getClass();


        for (Field field : c.getDeclaredFields()) {
            field.setAccessible(true);
            var fieldDescriber = StructureDescriber.describeFieldOrMethod(field);

            if (fieldDescriber.getType().equals(FieldType.OBJECT)) {
                if (field.get(o) != null)
                    evaluate((T) field.get(o), clazz);
            } else {
                checkAnnotations(field.get(o), fieldDescriber);
            }
        }


    }

    private void checkAnnotations(Object obj, Property fieldDescriber) {
        if (fieldDescriber.isRequired() && (obj == null || (obj instanceof Date && ((Date) obj).getTime() < 100000) || (obj instanceof String && ((String) obj).isEmpty()))) {
            throw new ElepyException(fieldDescriber.getPrettyName() + " is blank, please fill it in!");
        }
        if (fieldDescriber.getType().equals(FieldType.NUMBER)) {
            if (obj == null) {
                obj = 0;
            }
            if (!(obj instanceof java.lang.Number)) {
                throw new ElepyException(fieldDescriber.getPrettyName() + " must be a number");
            }
            java.lang.Number number = (java.lang.Number) obj;


            NumberPropertyConfig numberAnnotation = NumberPropertyConfig.of(fieldDescriber);
            if (number.floatValue() > numberAnnotation.getMaximum() || number.floatValue() < numberAnnotation.getMinimum()) {
                throw new ElepyException(String.format("%s must be between %d and %d", fieldDescriber.getPrettyName(), (int) numberAnnotation.getMinimum(), (int) numberAnnotation.getMaximum()));
            }

        }
        if (fieldDescriber.getType().equals(FieldType.TEXT)) {
            String text = (String) obj;
            if (text == null) {
                text = "";
            }
            TextPropertyConfig textAnnotation = TextPropertyConfig.of(fieldDescriber);
            if (text.length() > textAnnotation.getMaximumLength() || text.length() < textAnnotation.getMinimumLength()) {
                throw new ElepyException(String.format("%s must be between %d and %d characters long", fieldDescriber.getPrettyName(), textAnnotation.getMinimumLength(), textAnnotation.getMaximumLength()));
            }

        }
        if (fieldDescriber.getType().equals(FieldType.DATE)) {
            Date date = obj == null ? new Date(0) : (Date) obj;


            DatePropertyConfig dateTimeAnnotation = DatePropertyConfig.of(fieldDescriber);


            Date min = dateTimeAnnotation.getMinimumDate();
            Date max = dateTimeAnnotation.getMaximumDate();
            if (date.before(min) || date.after(max)) {
                throw new ElepyException(String.format("%s must be between '%s' and '%s'", fieldDescriber.getPrettyName(), dateTimeAnnotation.getMinimumDate(), dateTimeAnnotation.getMaximumDate()));

            }

        }
    }
}
