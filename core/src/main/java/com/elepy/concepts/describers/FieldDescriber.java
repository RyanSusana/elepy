package com.elepy.concepts.describers;

import com.elepy.annotations.Boolean;
import com.elepy.annotations.*;
import com.elepy.annotations.Number;
import com.elepy.models.FieldType;
import com.elepy.models.NumberType;
import com.elepy.models.TextType;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.jongo.marshall.jackson.oid.MongoId;

import javax.persistence.Column;
import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.util.HashMap;
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
        final Identifier identifier = field.getAnnotation(Identifier.class);
        if (jsonProperty != null) {
            return jsonProperty.value();
        } else if (mongoId != null || identifier != null) {
            return "id";
        } else {
            return field.getName();
        }
    }

    private void mapFieldAnnotations(Field field, Map<String, Object> fieldMap) {

        fieldMap.put("required", field.getAnnotation(Required.class) != null);

        final Column column = field.getAnnotation(Column.class);

        fieldMap.put("editable", !field.isAnnotationPresent(Uneditable.class) || (column!=null && !column.updatable()));

        Importance importance = field.getAnnotation(Importance.class);
        fieldMap.put("importance", importance == null ? 0 : importance.value());
    }

    private void mapFieldTypeInformation(Field field, Map<String, Object> fieldMap) {
        FieldType type = FieldType.guessType(field);

        fieldMap.put("type", type);

        if (type.equals(FieldType.ENUM)) {
            fieldMap.put("availableValues", field.getType().getEnumConstants());
        }
        if (type.equals(FieldType.OBJECT)) {
            fieldMap.put("objectName", field.getType().getSimpleName());

            fieldMap.put("fields", new StructureDescriber(field.getType()).getStructure());
        }
        if (type.equals(FieldType.TEXT)) {
            fieldMap.put("textType", field.getAnnotation(Text.class) != null ? field.getAnnotation(Text.class).value() : TextType.TEXTFIELD);
        }
        if (type.equals(FieldType.NUMBER)) {
            fieldMap.put("numberType", field.getAnnotation(Number.class) != null ? field.getAnnotation(Number.class).value() : NumberType.guessType(field));
        }
        if (type.equals(FieldType.BOOLEAN)) {
            final Boolean annotation = field.getAnnotation(Boolean.class);
            fieldMap.put("trueValue", annotation == null ? "true" : annotation.trueValue());
            fieldMap.put("falseValue", annotation == null ? "false" : annotation.falseValue());
        }
        if (type.equals(FieldType.OBJECT_ARRAY)) {

            final Class array = (Class) ((ParameterizedType) field.getGenericType()).getActualTypeArguments()[0];

            fieldMap.put("fields", new StructureDescriber(array));
            fieldMap.put("arrayType", array.getSimpleName());

        }
        if (type.equals(FieldType.ENUM_ARRAY)) {
            final Class array = (Class) ((ParameterizedType) field.getGenericType()).getActualTypeArguments()[0];
            fieldMap.put("availableValues", array.getEnumConstants());
        }
        if (type.equals(FieldType.PRIMITIVE_ARRAY)) {
            final Class array = (Class) ((ParameterizedType) field.getGenericType()).getActualTypeArguments()[0];
            fieldMap.put("primitiveType", FieldType.getUnannotatedFieldType(array));
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
