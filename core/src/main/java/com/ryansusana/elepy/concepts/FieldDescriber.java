package com.ryansusana.elepy.concepts;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.ryansusana.elepy.annotations.*;
import com.ryansusana.elepy.annotations.Number;
import com.ryansusana.elepy.models.FieldType;
import com.ryansusana.elepy.models.NumberType;
import com.ryansusana.elepy.models.TextType;
import org.jongo.marshall.jackson.oid.MongoId;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FieldDescriber {

    private final java.lang.reflect.Field field;

    private final Map<String, Object> fieldMap;

    private final boolean required;

    private final boolean editable;

    private final String name;

    private final String prettyName;

    private final FieldType type;

    public FieldDescriber(Field field) {
        this.field = field;
        name = name();
        prettyName = prettyName();
        this.fieldMap = mapField();

        required = (boolean) fieldMap.getOrDefault("required", false);
        editable = (boolean) fieldMap.getOrDefault("editable", true);
        type = (FieldType) fieldMap.get("type");
    }

    private String prettyName() {
        final PrettyName prettyName = field.getAnnotation(PrettyName.class);
        if (prettyName != null) {
            return prettyName.value();
        } else {
            return getName();
        }
    }

    private String name() {
        JsonProperty jsonProperty = field.getAnnotation(JsonProperty.class);
        MongoId mongoId = field.getAnnotation(MongoId.class);
        if (jsonProperty != null) {
            return jsonProperty.value();
        } else if (mongoId != null) {
            return "id";
        } else {
            return field.getName();
        }
    }

    private void mapFieldAnnotations(Field field, Map<String, Object> fieldMap) {
        RequiredField requiredField = field.getAnnotation(RequiredField.class);
        if (requiredField != null) {
            fieldMap.put("required", true);
        } else {
            fieldMap.put("required", false);
        }
        fieldMap.put("editable", field.getAnnotation(NonEditable.class) == null);
    }

    private void mapFieldTypeInformation(Field field, Map<String, Object> fieldMap) {
        FieldType type = FieldType.getByRepresentation(field);

        fieldMap.put("type", type);

        if (type.equals(FieldType.ENUM)) {
            fieldMap.put("availableValues", field.getType().getEnumConstants());
        }
        if (type.equals(FieldType.OBJECT)) {
            fieldMap.put("objectName", field.getType().getSimpleName());


            List<Map<String, Object>> innerFields = new ArrayList<>();
            for (Field innerField : field.getType().getDeclaredFields()) {

                innerFields.add(new FieldDescriber(innerField).getFieldMap());
            }
            fieldMap.put("fields", innerFields);
        }
        if (type.equals(FieldType.TEXT)) {
            fieldMap.put("textType", field.getAnnotation(Text.class) != null ? field.getAnnotation(Text.class).value() : TextType.TEXTFIELD);
        }
        if (type.equals(FieldType.NUMBER)) {
            fieldMap.put("numberType", field.getAnnotation(Number.class) != null ? field.getAnnotation(Number.class).value() : NumberType.guessType(field));
        }
    }

    public NumberType getNumberType() {
        if (!type.equals(FieldType.NUMBER)) {
            throw new IllegalArgumentException("This field is not a number");
        }
        return (NumberType) fieldMap.get("numberType");
    }

    public NumberType getTextType() {
        if (!type.equals(FieldType.NUMBER)) {
            throw new IllegalArgumentException("This field is not a string");
        }
        return (NumberType) fieldMap.get("textType");
    }

    public Map<String, Object> mapField() {
        Map<String, Object> fieldMap = new HashMap<>();


        fieldMap.put("name", getName());

        fieldMap.put("pretty_name", getPrettyName());
        mapFieldAnnotations(field, fieldMap);
        mapFieldTypeInformation(field, fieldMap);

        return fieldMap;
    }


    public Field getField() {
        return this.field;
    }

    public Map<String, Object> getFieldMap() {
        return this.fieldMap;
    }

    public boolean isRequired() {
        return this.required;
    }

    public String getName() {
        return this.name;
    }

    public String getPrettyName() {
        return this.prettyName;
    }

    public FieldType getType() {
        return this.type;
    }

    public boolean isEditable() {
        return editable;
    }

}
