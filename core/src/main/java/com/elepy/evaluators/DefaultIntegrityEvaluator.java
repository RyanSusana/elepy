package com.elepy.evaluators;

import com.elepy.annotations.ElepyConstructor;
import com.elepy.dao.Crud;
import com.elepy.dao.querymodel.Filters;
import com.elepy.exceptions.ElepyException;
import com.elepy.igniters.ModelContext;
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
            throw ElepyException.internalServerError();
        }
    }

    private void checkUniqueness(T item, Crud<T> dao, boolean insert) throws IllegalAccessException {

        List<Field> uniqueFields = ReflectionUtils.getUniqueFields(item.getClass());

        Optional<Serializable> id = ReflectionUtils.getId(item);

        if (insert && id.isPresent() && dao.getById(id.get()).isPresent()) {
            throw ElepyException.translated("{elepy.messages.exceptions.notUniqueField}",
                    ReflectionUtils.getLabel(ReflectionUtils.getIdFieldOrThrow(item.getClass())),
                    String.valueOf(id.get()));
        }

        for (Field field : uniqueFields) {
            field.setAccessible(true);
            Object prop = field.get(item);
            final var exceptionToThrow = ElepyException.translated("{elepy.messages.exceptions.notUniqueField}", ReflectionUtils.getLabel(field), String.valueOf(prop));
            final List<T> foundItems = dao.findLimited(10, Filters.eq(ReflectionUtils.getPropertyName(field), prop == null ? "" : prop.toString()));
            if (foundItems.size() > 0) {

                if (foundItems.size() > 1) {
                    throw exceptionToThrow;
                }

                T foundRecord = foundItems.get(0);
                final Optional<Serializable> foundId = ReflectionUtils.getId(foundRecord);
                if ((id.isPresent() || foundId.isPresent()) && !id.equals(foundId)) {
                    throw exceptionToThrow;
                }
            }
        }

    }
}
