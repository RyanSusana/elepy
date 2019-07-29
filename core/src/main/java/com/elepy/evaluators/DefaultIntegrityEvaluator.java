package com.elepy.evaluators;

import com.elepy.annotations.ElepyConstructor;
import com.elepy.dao.Crud;
import com.elepy.describers.ModelContext;
import com.elepy.exceptions.ElepyException;
import com.elepy.utils.ReflectionUtils;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.List;
import java.util.Optional;

public class DefaultIntegrityEvaluator<T> implements IntegrityEvaluator<T> {

    private final Crud<T> crud;

    public DefaultIntegrityEvaluator(ModelContext<T> modelContext) {
        this.crud = modelContext.getCrud();
    }

    @ElepyConstructor
    public DefaultIntegrityEvaluator(Crud<T> crud) {
        this.crud = crud;
    }

    @Override
    public void evaluate(T item, EvaluationType isACreate) {
        try {
            checkUniqueness(item, crud, isACreate.equals(EvaluationType.CREATE));
        } catch (IllegalAccessException e) {
            throw new ElepyException("Can't reflectively checkUniqueness()", 500);
        }
    }

    private void checkUniqueness(T item, Crud<T> dao, boolean insert) throws IllegalAccessException {

        List<Field> uniqueFields = ReflectionUtils.getUniqueFields(item.getClass());

        if (dao.count() == 0) {
            return;
        }

        Optional<Serializable> id = ReflectionUtils.getId(item);

        if (insert && id.isPresent() && dao.getById(id.get()).isPresent()) {
            throw new ElepyException("Duplicate ID's", 400);
        }

        for (Field field : uniqueFields) {
            field.setAccessible(true);
            Object prop = field.get(item);

            final List<T> foundItems = dao.searchInField(field, prop == null ? "" : prop.toString());
            if (foundItems.size() > 0) {

                if (foundItems.size() > 1) {
                    throw new ElepyException(String.format("An item with the %s: '%s' already exists in the system!", ReflectionUtils.getPrettyName(field), String.valueOf(prop)));
                }

                T foundRecord = foundItems.get(0);
                final Optional<Serializable> foundId = ReflectionUtils.getId(foundRecord);
                if ((id.isPresent() || foundId.isPresent()) && !id.equals(foundId)) {
                    throw new ElepyException(String.format("An item with the %s: '%s' already exists in the system!", ReflectionUtils.getPrettyName(field), String.valueOf(prop)));
                }
            }
        }

    }
}
