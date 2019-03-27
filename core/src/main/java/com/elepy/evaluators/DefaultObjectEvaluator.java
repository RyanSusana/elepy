package com.elepy.evaluators;

import com.elepy.annotations.DateTime;
import com.elepy.annotations.Number;
import com.elepy.annotations.Text;
import com.elepy.describers.FieldDescriber;
import com.elepy.exceptions.ElepyException;
import com.elepy.models.FieldType;
import com.elepy.utils.DateUtils;

import java.lang.reflect.Field;
import java.util.Date;

public class DefaultObjectEvaluator<T> implements ObjectEvaluator<T> {


    public void evaluate(T o, Class<T> clazz) throws Exception {

        Class c = o.getClass();


        for (Field field : c.getDeclaredFields()) {
            field.setAccessible(true);
            FieldDescriber fieldDescriber = new FieldDescriber(field);

            if (fieldDescriber.getType().equals(FieldType.OBJECT)) {
                if (field.get(o) != null)
                    evaluate((T) field.get(o), clazz);
            } else {
                checkAnnotations(field.get(o), fieldDescriber);
            }
        }


    }


    private void checkAnnotations(Object obj, FieldDescriber fieldDescriber) {
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
            if (fieldDescriber.getField().isAnnotationPresent(Number.class)) {
                Number numberAnnotation = fieldDescriber.getField().getAnnotation(Number.class);
                if (number.floatValue() > numberAnnotation.maximum() || number.floatValue() < numberAnnotation.minimum()) {
                    throw new ElepyException(String.format("%s must be between %d and %d", fieldDescriber.getPrettyName(), (int) numberAnnotation.minimum(), (int) numberAnnotation.maximum()));
                }
            }
        }
        if (fieldDescriber.getType().equals(FieldType.TEXT)) {
            String text = (String) obj;
            if (text == null) {
                text = "";
            }
            if (fieldDescriber.getField().isAnnotationPresent(Text.class)) {
                Text textAnnotation = fieldDescriber.getField().getAnnotation(Text.class);
                if (text.length() > textAnnotation.maximumLength() || text.length() < textAnnotation.minimumLength()) {
                    throw new ElepyException(String.format("%s must be between %d and %d characters long", fieldDescriber.getPrettyName(), textAnnotation.minimumLength(), textAnnotation.maximumLength()));
                }
            }
        }
        if (fieldDescriber.getType().equals(FieldType.DATE)) {
            Date date = (Date) obj;

            DateTime dateTimeAnnotation = fieldDescriber.getField().getAnnotation(DateTime.class);

            if (dateTimeAnnotation != null && date != null) {
                Date min = DateUtils.guessDate(dateTimeAnnotation.minimumDate());
                Date max = DateUtils.guessDate(dateTimeAnnotation.maximumDate());

                if (date.before(min) || date.after(max)) {
                    throw new ElepyException(String.format("%s must be between '%s' and '%s'", fieldDescriber.getPrettyName(), dateTimeAnnotation.minimumDate(), dateTimeAnnotation.maximumDate()));

                }
            }
        }
    }
}
