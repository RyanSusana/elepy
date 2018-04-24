package com.ryansusana.elepy.concepts;

import com.ryansusana.elepy.models.RestErrorMessage;

import java.lang.reflect.Field;

public class ObjectUpdateEvaluatorImpl implements ObjectUpdateEvaluator {
    @Override
    public void evaluate(Object before, Object updated) throws IllegalAccessException {
        for (Field field : before.getClass().getDeclaredFields()) {
            field.setAccessible(true);
            FieldDescriber describer = new FieldDescriber(field);
            if (!describer.isEditable()) {
                if (!field.get(before).equals(field.get(updated))) {
                    throw new RestErrorMessage("Not allowed to edit: " + describer.getPrettyName());
                }
            }
        }
    }
}
