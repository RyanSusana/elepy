package com.elepy.mongo.querybuilding;

import com.elepy.annotations.Searchable;
import com.elepy.annotations.Unique;
import com.elepy.exceptions.ElepyConfigException;
import com.elepy.utils.ReflectionUtils;
import com.google.common.base.Strings;
import org.jongo.marshall.jackson.oid.MongoId;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class MongoSearch {

    private final String qry;
    private final Class<?> cls;

    private String compiled;

    private List<Field> searchableFields;

    public MongoSearch(String qry, Class<?> cls) {
        this.qry = qry;
        this.cls = cls;

        this.searchableFields = getSearchableFields();
    }

    public String getQuery() {
        return qry;
    }

    private List<Field> getSearchableFields() {
        List<Field> fields = ReflectionUtils.searchForFieldsWithAnnotation(cls, Searchable.class, MongoId.class, Unique.class);

        fields.add(ReflectionUtils.getIdField(cls).orElseThrow(() -> new ElepyConfigException("No id field")));
        return fields;
    }

    public String compile() {
        if (compiled == null) {
            String searchRegex = searchableFields.stream()
                    .map(field -> String.format("{%s: {$regex: #, $options: 'i'}}", ReflectionUtils.getPropertyName(field)))
                    .collect(Collectors.joining(","));

            compiled = String.format("{$or: [%s]}", searchRegex);
        }
        return compiled;
    }

    public Serializable[] getParameters() {

        if (Strings.isNullOrEmpty(qry)) {
            return new Serializable[0];
        }
        return searchableFields.stream()
                .map(field -> Pattern.compile(".*" + qry + ".*", Pattern.CASE_INSENSITIVE).toString())
                .toArray(Serializable[]::new);

    }
}
