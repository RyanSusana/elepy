package com.elepy.mongo;

import com.elepy.annotations.Searchable;
import com.elepy.annotations.Unique;
import com.elepy.exceptions.ElepyConfigException;
import com.elepy.utils.ReflectionUtils;
import org.jongo.marshall.jackson.oid.MongoId;

import java.lang.reflect.Field;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class MongoSearch {

    private final String qry;
    private final Class<?> cls;

    private String compiled;

    public MongoSearch(String qry, Class<?> cls) {
        this.qry = qry;
        this.cls = cls;
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
            final Pattern pattern = Pattern.compile(".*" + qry + ".*", Pattern.CASE_INSENSITIVE);

            String patternCompiled = pattern.toString();
            String searchRegex = getSearchableFields().stream().map(field -> {

                String propertyName = ReflectionUtils.getPropertyName(field);


                return String.format("{%s: {$regex: '%s', $options: 'i'}}", propertyName, patternCompiled);

            }).collect(Collectors.joining(","));


            compiled = String.format("{$or: [%s]}", searchRegex);
        }
        return compiled;
    }

} 
