package com.elepy.hibernate;

import com.elepy.annotations.Searchable;
import com.elepy.dao.BooleanGroup;
import com.elepy.dao.Expression;
import com.elepy.dao.Filter;
import com.elepy.dao.SearchQuery;
import com.elepy.exceptions.ElepyConfigException;
import com.elepy.exceptions.ElepyException;
import com.elepy.models.FieldType;
import com.elepy.models.Property;
import com.elepy.models.Schema;
import com.elepy.utils.MapperUtils;
import com.elepy.utils.ReflectionUtils;

import javax.persistence.Column;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

public class HibernateQueryFactory<T> {

    private final Schema<T> schema;
    private final Root<T> root;
    private final CriteriaBuilder cb;

    public HibernateQueryFactory(Schema<T> schema, Root<T> root, CriteriaBuilder cb) {
        this.schema = schema;
        this.root = root;
        this.cb = cb;
    }

    private Predicate booleanGroup(BooleanGroup group) {

        if (group.getOperator().equals(BooleanGroup.BooleanOperator.OR)) {
            return cb.or(group.getExpressions().stream()
                    .map(this::generatePredicate)
                    .toArray(Predicate[]::new)
            );
        } else {
            return cb.and(group.getExpressions().stream()
                    .map(this::generatePredicate)
                    .toArray(Predicate[]::new)
            );
        }
    }

    public Predicate generatePredicate(Expression qry) {

        if (qry instanceof Filter) {
            return filter((Filter) qry);
        } else if (qry instanceof SearchQuery) {
            return search((SearchQuery) qry);
        } else {
            return booleanGroup((BooleanGroup) qry);
        }
    }


    private Predicate filter(Filter filter) {

        final Property property = schema.getProperty(filter.getPropertyName());
        final FieldType fieldType = property.getType();

        final Field field;

        field = ReflectionUtils.getPropertyField(schema.getJavaClass(), property.getName());
        final Serializable value = value(field, property, filter.getFilterValue().toString());
        final String fieldName = property.getJavaName();
        switch (filter.getFilterType()) {
            case EQUALS:
                return cb.equal(root.get(fieldName), value);
            case NOT_EQUALS:
                return cb.notEqual(root.get(fieldName), value);
            case CONTAINS:
                if (fieldType.equals(FieldType.ARRAY)) {
                    throw new ElepyException("The 'contains' filter can't be applied to arrays (yet)");
                } else {
                    return cb.like(root.get(fieldName), "%" + value + "%");
                }
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
            case IS_NULL:
                return cb.isNull(root.get(fieldName));
            case NOT_NULL:
                return cb.isNotNull(root.get(fieldName));
            case STARTS_WITH:
                return cb.like(root.get(fieldName), value + "%");
        }
        throw new ElepyException("Hibernate does not support the filter: " + filter.getFilterType().getName());
    }

    private Predicate search(SearchQuery searchQuery) {
        return cb.or(getSearchPredicates(searchQuery.getTerm()).toArray(new Predicate[0]));
    }

    private List<Predicate> getSearchPredicates(String term) {
        if (term == null || term.trim().isEmpty()) {
            //Always true
            return Collections.singletonList(cb.and());
        }
        return getSearchableFields().stream()
                .map(field -> cb.like(cb.lower(root.get(getJPAFieldName(field))), cb.literal("%" + term.toLowerCase() + "%")))
                .collect(Collectors.toList());
    }

    private static Serializable value(Field field, Property property, String value) {
        return MapperUtils.toValueFromString(field, property.getType(), value);
    }

    private String getJPAFieldName(Field field) {
        Column annotation = com.elepy.utils.Annotations.get(field,Column.class);

        if (annotation != null && !annotation.name().isEmpty()) {
            return annotation.name();
        }

        return field.getName();
    }


    private List<Field> getSearchableFields() {
        List<Field> fields = ReflectionUtils.searchForFieldsWithAnnotation(schema.getJavaClass(), Searchable.class);

        Field idProperty = ReflectionUtils.getIdField(schema.getJavaClass()).orElseThrow(() -> new ElepyConfigException("No id idProperty"));
        fields.add(idProperty);


        fields.removeIf(field -> !field.getType().equals(String.class));
        return fields;
    }
}
