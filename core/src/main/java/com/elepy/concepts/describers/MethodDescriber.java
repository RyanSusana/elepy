package com.elepy.concepts.describers;

import com.elepy.annotations.Number;
import com.elepy.annotations.PrettyName;
import com.elepy.annotations.RequiredField;
import com.elepy.annotations.Text;
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
        this.fieldMap = mapField();

        required = (boolean) fieldMap.getOrDefault("required", false);
        editable = false;
        type = (FieldType) fieldMap.get("type");
    }

    private void mapMethodTypeInfo(Method method, Map<String, Object> fieldMap) {

        FieldType type = FieldType.getByRepresentation(method);

        fieldMap.put("type", type);

        if (type.equals(FieldType.ENUM)) {
            fieldMap.put("availableValues", method.getReturnType().getEnumConstants());
        } else if (type.equals(FieldType.OBJECT)) {
            fieldMap.put("objectName", method.getReturnType().getSimpleName());

            fieldMap.put("fields", new StructureDescriber(method.getReturnType()).getStructure());
        } else if (type.equals(FieldType.TEXT)) {
            fieldMap.put("textType", method.getAnnotation(Text.class) != null ? method.getAnnotation(Text.class).value() : TextType.TEXTFIELD);
        } else if (type.equals(FieldType.NUMBER)) {
            fieldMap.put("numberType", method.getAnnotation(Number.class) != null ? method.getAnnotation(Number.class).value() : NumberType.guessType(method.getReturnType()));
        } else {
            throw new UnsupportedOperationException("Collections are not supported for method annotations.");
        }


    }

    public Map<String, Object> mapField() {
        Map<String, Object> fieldMap = new HashMap<>();


        fieldMap.put("name", getName());

        fieldMap.put("pretty_name", getPrettyName());
        mapMethodAnnotations(method, fieldMap);
        mapMethodTypeInfo(method, fieldMap);

        return fieldMap;
    }

    private String prettyName() {
        final PrettyName prettyName = method.getAnnotation(PrettyName.class);
        if (prettyName != null) {
            return prettyName.value();
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

        fieldMap.put("required", method.getAnnotation(RequiredField.class) != null);
        fieldMap.put("editable", false);

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
