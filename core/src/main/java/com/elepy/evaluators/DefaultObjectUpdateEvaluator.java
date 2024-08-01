package com.elepy.evaluators;

import com.elepy.exceptions.ElepyException;
import com.elepy.schemas.Property;
import com.elepy.schemas.PropertyFactory;

import java.lang.reflect.Field;

public class DefaultObjectUpdateEvaluator<T> implements ObjectUpdateEvaluator<T> {
    @Override
    public void evaluate(T before, T updated) throws IllegalAccessException {
        for (Field field : before.getClass().getDeclaredFields()) {
            field.setAccessible(true);
            Property describer = new PropertyFactory().describeAccessibleObject(field);
            if (!describer.isEditable() && !field.get(before).equals(field.get(updated))) {
                throw ElepyException.translated("{elepy.messages.exceptions.notAllowedToEditField}" ,describer.getLabel());
            }
        }
    }
}
