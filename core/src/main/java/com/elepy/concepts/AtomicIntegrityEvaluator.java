package com.elepy.concepts;

import com.elepy.exceptions.RestErrorMessage;
import com.elepy.utils.ClassUtils;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class AtomicIntegrityEvaluator<T> {
    public void evaluate(List<T> items) throws IllegalAccessException {

        for (T item : items) {

            List<T> theRest = new ArrayList<>(items);

            theRest.remove(item);

            checkUniqueness(item, theRest);
        }
    }

    private void checkUniqueness(T item, List<T> theRest) throws IllegalAccessException {
        List<Field> uniqueFields = ClassUtils.getUniqueFields(item.getClass());
        Optional<String> id = ClassUtils.getId(item);

        for (Field field : uniqueFields) {

            field.setAccessible(true);
            Object prop = field.get(item);


            final List<T> foundItems = theRest.stream().filter(t -> {

                try {
                    return field.get(t).equals(prop);
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                    return false;
                }
            }).collect(Collectors.toList());

            integrityCheck(foundItems, id, field);
        }
    }

    private void integrityCheck(List<T> foundItems, Optional<String> id, Field field) {
        if (!foundItems.isEmpty()) {


            if (foundItems.size() > 1) {
                throw new RestErrorMessage(String.format("There are duplicates with the %s: '%s' in the given array!", ClassUtils.getPrettyName(field), String.valueOf(prop)));
            }

            T foundRecord = foundItems.get(0);
            final Optional<String> foundId = ClassUtils.getId(foundRecord);
            if (id.isPresent() || foundId.isPresent()) {
                if (!id.equals(foundId)) {
                    throw new RestErrorMessage(String.format("An item with the %s: '%s' already exists in the system!", ClassUtils.getPrettyName(field), String.valueOf(prop)));
                }
            } else {
                throw new RestErrorMessage(String.format("There are duplicates with the %s: '%s' in the given array!", ClassUtils.getPrettyName(field), String.valueOf(prop)));

            }
        }
    }
}
