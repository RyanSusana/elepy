package com.elepy.mongo;

import com.elepy.annotations.Identifier;
import com.fasterxml.jackson.databind.PropertyName;
import com.fasterxml.jackson.databind.introspect.Annotated;
import com.fasterxml.jackson.databind.introspect.NopAnnotationIntrospector;
import com.fasterxml.jackson.databind.type.TypeFactory;

import javax.persistence.Id;
import java.util.List;

public class CustomAnnotationIntrospector extends NopAnnotationIntrospector {


    public CustomAnnotationIntrospector(TypeFactory typeFactory) {

    }
    @Override
    public List<PropertyName> findPropertyAliases(Annotated ann) {
        if (isId(ann)) {
            return List.of(new PropertyName("_id"), new PropertyName("id"), new PropertyName(ann.getName()));
        }
        return super.findPropertyAliases(ann);
    }

    private boolean isId(Annotated annotated) {
        return annotated.hasAnnotation(Id.class)
                || annotated.hasAnnotation(javax.persistence.Id.class)
                || annotated.hasAnnotation(Identifier.class)
                || annotated.getName().equals("id");

    }

    // Handling of javax.persistence.Id
    @Override
    public PropertyName findNameForDeserialization(Annotated a) {
        String rawName = findPropertyName(a);
        if (rawName != null) {
            return new PropertyName(rawName);
        }
        return null;
    }

    @Override
    public PropertyName findNameForSerialization(Annotated a) {

        String rawName = findPropertyName(a);
        if (rawName != null) {
            return new PropertyName(rawName);
        }
        return null;

    }

    private String findPropertyName(Annotated annotated) {

        if (isId(annotated)) {
            return "_id";
        }
        return null;
    }


}
