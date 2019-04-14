package com.elepy.hibernate;

import com.elepy.dao.FilterQuery;
import com.elepy.dao.FilterableField;
import com.elepy.exceptions.ElepyException;
import com.elepy.models.FieldType;
import com.elepy.models.NumberType;
import com.elepy.utils.DateUtils;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.io.Serializable;
import java.util.Date;

public class HibernatePredicateFactory {

    public static Predicate fromFilter(Root root, CriteriaBuilder cb, FilterQuery filterQuery) {
        final Serializable value = value(filterQuery.getFilterableField(), filterQuery.getFilterValue());
        final FieldType fieldType = filterQuery.getFilterableField().getFieldType();

        final String fieldName = filterQuery.getFilterableField().getField().getName();
        switch (filterQuery.getFilterType()) {
            case EQUALS:
                return cb.equal(root.get(fieldName), value);
            case NOT_EQUALS:
                return cb.notEqual(root.get(fieldName), value);
            case CONTAINS:
                return cb.like(root.get(fieldName), "%" + value + "%");
            case GREATER_THAN:
                if (fieldType.equals(FieldType.DATE)) {
                    return cb.greaterThan(root.get(fieldName).as(Date.class), (Date) value);
                } else {
                    return cb.gt(root.get(fieldName), (Number) value);
                }
            case LESSER_THAN:
                if (fieldType.equals(FieldType.DATE)) {
                    return cb.lessThan(root.get(fieldName).as(Date.class), (Date) value);
                } else {
                    return cb.lt(root.get(fieldName), (Number) value);
                }
            case GREATER_THAN_OR_EQUALS:

                if (fieldType.equals(FieldType.DATE)) {
                    return cb.greaterThanOrEqualTo(root.get(fieldName).as(Date.class), (Date) value);
                } else {
                    return cb.ge(root.get(fieldName), (Number) value);
                }
            case LESSER_THAN_OR_EQUALS:
                if (fieldType.equals(FieldType.DATE)) {
                    return cb.lessThanOrEqualTo(root.get(fieldName).as(Date.class), (Date) value);
                } else {
                    return cb.le(root.get(fieldName), (Number) value);
                }
        }
        throw new ElepyException("Hibernate does not support the filter: " + filterQuery.getFilterType().getQueryName());
    }

    private static Serializable value(FilterableField field, String value) {
        if (field.getFieldType().equals(FieldType.NUMBER)) {
            NumberType numberType = NumberType.guessType(field.getField());
            if (numberType.equals(NumberType.INTEGER)) {
                return Long.parseLong(value);
            } else {
                return Float.parseFloat(value);
            }
        } else if (field.getFieldType().equals(FieldType.DATE)) {

            Date date = DateUtils.guessDate(value);

            if (date == null) {
                return value;
            } else {
                return date;
            }
        } else {
            return value;
        }
    }
}
