package com.ryansusana.elepy.concepts;

import com.ryansusana.elepy.annotations.Number;
import com.ryansusana.elepy.annotations.Text;
import com.ryansusana.elepy.models.FieldType;
import com.ryansusana.elepy.models.RestErrorMessage;

import java.lang.reflect.Field;
import java.util.Date;

public class ObjectEvaluatorImpl implements ObjectEvaluator {


    public void evaluate(Object o) throws Exception {

        Class c = o.getClass();


        for (Field field : c.getDeclaredFields()) {
            field.setAccessible(true);
            FieldDescriber fieldDescriber = new FieldDescriber(field);

            if (fieldDescriber.getType().equals(FieldType.OBJECT)) {
                if (field.get(o) != null)
                    evaluate(field.get(o));
            } else {
                checkAnnotations(field.get(o), fieldDescriber);
            }
        }


    }


    private void checkAnnotations(Object obj, FieldDescriber fieldDescriber) {
        if (fieldDescriber.isRequired() && (obj == null || (obj instanceof Date && ((Date) obj).getTime() < 100000) || (obj instanceof String && ((String) obj).isEmpty()))) {
            throw new RestErrorMessage(fieldDescriber.getPrettyName() + " is blank, please fill it in!");
        }
        if (fieldDescriber.getType().equals(FieldType.NUMBER)) {
            if (!(obj instanceof java.lang.Number)) {
                throw new RestErrorMessage(fieldDescriber.getPrettyName() + " must be a number");
            }
            java.lang.Number number = (java.lang.Number) obj;
            if (fieldDescriber.getField().isAnnotationPresent(Number.class)) {
                Number numberAnnotation = fieldDescriber.getField().getAnnotation(Number.class);
                if (number.floatValue() > numberAnnotation.maximum() || number.floatValue() < numberAnnotation.minimum()) {
                    throw new RestErrorMessage(String.format("%s must be between %d and %d", fieldDescriber.getPrettyName(), (int) numberAnnotation.minimum(), (int) numberAnnotation.maximum()));
                }
            }
        }
        if (fieldDescriber.getType().equals(FieldType.TEXT)) {
            String text = (String) obj;
            if (fieldDescriber.getField().isAnnotationPresent(Text.class)) {
                Text textAnnotation = fieldDescriber.getField().getAnnotation(Text.class);
                if (text.length() >= textAnnotation.maximumLength() || text.length() <= textAnnotation.minimumLength()) {
                    throw new RestErrorMessage(String.format("%s must be between %d and %d characters long", fieldDescriber.getPrettyName(), textAnnotation.minimumLength(), textAnnotation.maximumLength()));
                }
            }
        }
    }
}
