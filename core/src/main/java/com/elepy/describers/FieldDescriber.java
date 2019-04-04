package com.elepy.describers;

import com.elepy.annotations.Number;
import com.elepy.annotations.*;
import com.elepy.models.FieldType;
import com.elepy.models.NumberType;
import com.elepy.models.TextType;
import com.elepy.utils.ClassUtils;
import com.elepy.utils.DateUtils;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.jongo.marshall.jackson.oid.MongoId;

import javax.persistence.Column;
import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.text.SimpleDateFormat;
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

    private final boolean idField;

    private SimpleDateFormat cmsFormat = new SimpleDateFormat("yyyy-MM-dd");

    public FieldDescriber(Field field) {
        this.field = field;
        idField = ClassUtils.getIdField(field.getDeclaringClass()).map(field1 -> field1.getName().equals(field.getName())).orElse(false);
        name = name();
        prettyName = prettyName();
        this.fieldMap = mapField();

        required = (boolean) fieldMap.getOrDefault("required", false);
        editable = (boolean) fieldMap.getOrDefault("editable", true);
        type = (FieldType) fieldMap.get("type");


    }

    private String prettyName() {
        final PrettyName prettyNameAnnotation = field.getAnnotation(PrettyName.class);
        if (prettyNameAnnotation != null) {
            return prettyNameAnnotation.value();
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

        fieldMap.put("editable", !idField && (!field.isAnnotationPresent(Uneditable.class) || (column != null && !column.updatable())));

        Importance importance = field.getAnnotation(Importance.class);
        fieldMap.put("importance", importance == null ? 0 : importance.value());


        fieldMap.put("unique", field.isAnnotationPresent(Unique.class) || (column != null && column.unique()));


        fieldMap.put("generated", (field.isAnnotationPresent(Generated.class) || (idField && !field.isAnnotationPresent(Identifier.class)) || (idField && field.isAnnotationPresent(Identifier.class) && field.getAnnotation(Identifier.class).generated())));
    }

    private void mapFieldTypeInformation(Field field, Map<String, Object> fieldMap) {
        FieldType fieldType = FieldType.guessType(field);

        fieldMap.put("type", fieldType);

        if (fieldType.equals(FieldType.ENUM)) {
            fieldMap.put("availableValues", ClassDescriber.getEnumMap(field.getType()));
        }
        if (fieldType.equals(FieldType.DATE)) {
            DateTime annotation = field.getAnnotation(DateTime.class);

            if (annotation != null) {
                fieldMap.put("includeTime", annotation.includeTime());
                fieldMap.put("minimumDate", cmsFormat.format(DateUtils.guessDate(annotation.minimumDate())));
                fieldMap.put("maximumDate", cmsFormat.format(DateUtils.guessDate(annotation.maximumDate())));
            } else {
                fieldMap.put("includeTime", true);
                fieldMap.put("minimumDate", null);
                fieldMap.put("maximumDate", null);
            }
        }
        if (fieldType.equals(FieldType.OBJECT)) {
            fieldMap.put("objectName", field.getType().getSimpleName());

            fieldMap.put("fields", new ClassDescriber(field.getType()).getStructure());
        }
        if (fieldType.equals(FieldType.TEXT)) {
            fieldMap.put("textType", field.getAnnotation(Text.class) != null ? field.getAnnotation(Text.class).value() : TextType.TEXTFIELD);
        }
        if (fieldType.equals(FieldType.NUMBER)) {
            fieldMap.put("numberType", field.getAnnotation(Number.class) != null ? field.getAnnotation(Number.class).value() : NumberType.guessType(field));
        }
        if (fieldType.equals(FieldType.BOOLEAN)) {
            final TrueFalse annotation = field.getAnnotation(TrueFalse.class);
            fieldMap.put("trueValue", annotation == null ? "true" : annotation.trueValue());
            fieldMap.put("falseValue", annotation == null ? "false" : annotation.falseValue());
        }
        if (fieldType.equals(FieldType.OBJECT_ARRAY)) {

            final Class array = (Class) ((ParameterizedType) field.getGenericType()).getActualTypeArguments()[0];

            fieldMap.put("fields", new ClassDescriber(array));
            fieldMap.put("arrayType", array.getSimpleName());

        }
        if (fieldType.equals(FieldType.ENUM_ARRAY)) {
            final Class<?> array = (Class) ((ParameterizedType) field.getGenericType()).getActualTypeArguments()[0];
            fieldMap.put("availableValues", ClassDescriber.getEnumMap(array));
        }
        if (fieldType.equals(FieldType.PRIMITIVE_ARRAY)) {
            final Class array = (Class) ((ParameterizedType) field.getGenericType()).getActualTypeArguments()[0];
            fieldMap.put("primitiveType", FieldType.getUnannotatedFieldType(array));
        }
    }


    public Map<String, Object> mapField() {
        Map<String, Object> newFieldMap = new HashMap<>();


        newFieldMap.put("name", getName());

        newFieldMap.put("prettyName", getPrettyName());
        mapFieldAnnotations(field, newFieldMap);
        mapFieldTypeInformation(field, newFieldMap);

        return newFieldMap;
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
