package com.ryansusana.elepy.concepts;

import com.ryansusana.elepy.annotations.Unique;
import com.ryansusana.elepy.dao.Crud;
import com.ryansusana.elepy.dao.SearchSetup;
import com.ryansusana.elepy.models.RestErrorMessage;

import java.lang.reflect.Field;
import java.util.List;

public class IntegrityEvaluatorImpl<T> implements IntegrityEvaluator<T> {
    @Override
    public void evaluate(T item, Crud<T> dao) throws IllegalAccessException {


        checkUniqueness(item, dao);
    }

    private void checkUniqueness(T item, Crud<T> dao) throws IllegalAccessException {
        List<Field> uniqueFields = FieldUtils.searchForFieldsWithAnnotation(item.getClass(), Unique.class);


        String id = FieldUtils.getId(item);
        assert id != null;
        for (Field field : uniqueFields) {

            Object prop = field.get(item);
            final List<T> foundItems = dao.search(String.format("{%s: #}", FieldUtils.getPropertyName(field)), prop);
            if (foundItems.size() > 0) {


                if (foundItems.size() > 1) {
                    throw new RestErrorMessage(String.format("An item with the %s: '%s' already exists in the system!", FieldUtils.getPrettyName(field), String.valueOf(prop)));
                }

                T foundRecord = foundItems.get(0);

                if (!id.equals(FieldUtils.getId(foundRecord))) {
                    throw new RestErrorMessage(String.format("An item with the %s: '%s' already exists in the system!", FieldUtils.getPrettyName(field), String.valueOf(prop)));
                }
            }
        }


    }


}
