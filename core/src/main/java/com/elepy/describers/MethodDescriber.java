package com.elepy.describers;

import com.elepy.annotations.Number;
import com.elepy.annotations.*;
import com.elepy.models.FieldType;
import com.elepy.models.NumberType;
import com.elepy.models.TextType;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.jongo.marshall.jackson.oid.MongoId;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

public class MethodDescriber {
    private final Method method;


    private final boolean required;

    private final boolean editable;

    private final String name;

    private final String prettyName;

    private final FieldType type;

    private final Map<String, Object> fieldMap;

    public MethodDescriber(Method method) {
        this.method = method;
        name = name();
        prettyName = prettyName();
        fieldMap = mapField();

        required = (boolean) fieldMap.getOrDefault("required", false);
        editable = false;
        type = (FieldType) fieldMap.get("type");


    }

    private void mapMethodTypeInfo(Method method, Map<String, Object> fieldMap) {

        FieldType fieldType = FieldType.guessType(method);

        fieldMap.put("type", fieldType);
        fieldMap.put("generated", true);

        if (fieldType.equals(FieldType.BOOLEAN)) {
            final TrueFalse annotation = method.getAnnotation(TrueFalse.class);
            fieldMap.put("trueValue", annotation == null ? "true" : annotation.trueValue());
            fieldMap.put("falseValue", annotation == null ? "false" : annotation.falseValue());
        } else if (fieldType.equals(FieldType.ENUM)) {
            fieldMap.put("availableValues", ClassDescriber.getEnumMap(method.getReturnType()));
        } else if (fieldType.equals(FieldType.OBJECT)) {
            fieldMap.put("objectName", method.getReturnType().getSimpleName());

            fieldMap.put("fields", new ClassDescriber(method.getReturnType()).getStructure());
        } else if (fieldType.equals(FieldType.TEXT)) {
            fieldMap.put("textType", method.getAnnotation(Text.class) != null ? method.getAnnotation(Text.class).value() : TextType.TEXTFIELD);
        } else if (fieldType.equals(FieldType.NUMBER)) {
            fieldMap.put("numberType", method.getAnnotation(Number.class) != null ? method.getAnnotation(Number.class).value() : NumberType.guessType(method.getReturnType()));
        } else {
            throw new UnsupportedOperationException("Collections are not supported for method annotations.");
        }


    }

    public Map<String, Object> mapField() {
        Map<String, Object> newFieldMap = new HashMap<>();


        newFieldMap.put("name", getName());

        newFieldMap.put("prettyName", getPrettyName());
        mapMethodAnnotations(method, newFieldMap);
        mapMethodTypeInfo(method, newFieldMap);

        return newFieldMap;
    }

    private String prettyName() {
        final PrettyName prettyNameAnnotation = method.getAnnotation(PrettyName.class);
        if (prettyNameAnnotation != null) {
            return prettyNameAnnotation.value();
        } else {
            return getName();
        }
    }

    private String name() {
        JsonProperty jsonProperty = method.getAnnotation(JsonProperty.class);
        MongoId mongoId = method.getAnnotation(MongoId.class);
        if (jsonProperty != null) {
            return jsonProperty.value();
        } else if (mongoId != null) {
            return "id";
        } else {
            return method.getName();
        }
    }

    private void mapMethodAnnotations(Method field, Map<String, Object> fieldMap) {

        fieldMap.put("required", method.getAnnotation(Required.class) != null);
        fieldMap.put("editable", false);

        Importance importance = field.getAnnotation(Importance.class);
        fieldMap.put("importance", importance == null ? 0 : importance.value());

    }

    public Method getMethod() {
        return method;
    }

    public boolean isRequired() {
        return required;
    }

    public boolean isEditable() {
        return editable;
    }

    public String getName() {
        return name;
    }

    public String getPrettyName() {
        return prettyName;
    }

    public FieldType getType() {
        return type;
    }

    public Map<String, Object> getFieldMap() {
        return fieldMap;
    }
}
