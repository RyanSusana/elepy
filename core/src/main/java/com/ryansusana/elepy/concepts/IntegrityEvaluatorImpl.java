package com.ryansusana.elepy.concepts;

import com.ryansusana.elepy.annotations.Unique;
import com.ryansusana.elepy.dao.Crud;
import com.ryansusana.elepy.models.RestErrorMessage;
import com.ryansusana.elepy.utils.ClassUtils;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Optional;

public class IntegrityEvaluatorImpl<T> implements IntegrityEvaluator<T> {
    @Override
    public void evaluate(T item, Crud<T> dao) throws IllegalAccessException {


        checkUniqueness(item, dao);
    }

    private void checkUniqueness(T item, Crud<T> dao) throws IllegalAccessException {
        List<Field> uniqueFields = ClassUtils.searchForFieldsWithAnnotation(item.getClass(), Unique.class);


        Optional<String> id = ClassUtils.getId(item);
        if (!id.isPresent()) {
            throw new IllegalStateException("No id present");
        }
        for (Field field : uniqueFields) {

            Object prop = field.get(item);
            final List<T> foundItems = dao.search(String.format("{%s: #}", ClassUtils.getPropertyName(field)), prop);
            if (foundItems.size() > 0) {


                if (foundItems.size() > 1) {
                    throw new RestErrorMessage(String.format("An item with the %s: '%s' already exists in the system!", ClassUtils.getPrettyName(field), String.valueOf(prop)));
                }

                T foundRecord = foundItems.get(0);

                if (!id.equals(ClassUtils.getId(foundRecord))) {
                    throw new RestErrorMessage(String.format("An item with the %s: '%s' already exists in the system!", ClassUtils.getPrettyName(field), String.valueOf(prop)));
                }
            }
        }


    }


}
