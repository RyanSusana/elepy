package com.elepy.dao.jongo;

import com.elepy.annotations.Searchable;
import com.elepy.annotations.Unique;
import com.elepy.exceptions.ElepyConfigException;
import com.elepy.utils.ClassUtils;
import org.jongo.marshall.jackson.oid.MongoId;

import java.lang.reflect.Field;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class MongoSearch {

    private final String qry;
    private final Class<?> cls;

    public MongoSearch(String qry, Class<?> cls) {
        this.qry = qry;
        this.cls = cls;
    }

    private List<Field> getSearchableFields() {
        List<Field> fields = ClassUtils.searchForFieldsWithAnnotation(cls, Searchable.class, MongoId.class, Unique.class);

        fields.add(ClassUtils.getIdField(cls).orElseThrow(() -> new ElepyConfigException("No id field")));
        return fields;
    }

    public String compile() {
        final Pattern pattern = Pattern.compile(".*" + qry + ".*", Pattern.CASE_INSENSITIVE);

        String patternCompiled = pattern.toString();
        String searchRegex = getSearchableFields().stream().map(field -> {

            String propertyName = ClassUtils.getPropertyName(field);


            return String.format("{%s: {$regex: '%s'}}", propertyName, patternCompiled);
        }).collect(Collectors.joining(","));


        return String.format("{$or: [%s]}", searchRegex);
    }

} 
