package com.elepy.schemas;

import com.elepy.annotations.DateTime;
import com.elepy.annotations.Label;
import com.elepy.annotations.TrueFalse;
import com.elepy.exceptions.ElepyConfigException;
import com.elepy.utils.Annotations;
import com.elepy.utils.DateUtils;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.*;

public class FieldMapper {
    public  Serializable toValueFromString(Field field, FieldType fieldType, String value) {
        if (FieldType.ENUM.equals(fieldType)) {
            return toEnumFromString(field, value);
        }
        if (FieldType.BOOLEAN.equals(fieldType)) {
            return toBooleanFromString(field, value);
        }
        if (FieldType.NUMBER.equals(fieldType)) {
            return toNumberFromString(field, value);
        } else if (FieldType.DATE.equals(fieldType)) {
            final DateTime annotation = Annotations.get(field, DateTime.class);
            final String format;

            if (annotation == null) {
                format = "";
            } else {
                format = annotation.format();
            }
            return toDateFromString(value, format);
        } else {
            return value;
        }
    }


    /**
     * This method goes through an Enum's fields and maps the Label of the field to the value of the Enum
     *
     * @param enumClass The class of the enum
     */
    public  List<Map<String, Object>> getEnumMapValues(Class<? extends Enum<?>> enumClass) {

        List<Map<String, Object>> toReturn = new ArrayList<>();
        for (Enum<?> enumConstant : enumClass.getEnumConstants()) {
            Map<String, Object> toAdd = new HashMap<>();

            toAdd.put("enumValue", enumConstant.name());

            Field declaredField;
            try {
                declaredField = enumClass.getDeclaredField(enumConstant.name());
            } catch (NoSuchFieldException ignored) {
                throw new ElepyConfigException("Enum field not found");
            }

            Label annotation = Annotations.get(declaredField, Label.class);
            if (annotation != null) {

                toAdd.put("enumName", annotation.value());
            } else {
                toAdd.put("enumName", enumConstant.name());
            }
            toReturn.add(toAdd);

        }
        return toReturn;
    }
    private  Serializable toEnumFromString(Field field, String value) {
        final List<Map<String, Object>> enumMapList = getEnumMapValues((Class<? extends Enum<?>>) field.getType());
        final Optional<Map<String, Object>> enumValue = enumMapList.stream().filter(enumMap -> enumMap.get("enumValue").toString().equalsIgnoreCase(value)).findFirst();


        if (enumValue.isPresent()) {
            return enumValue.get().get("enumValue").toString();
        }
        return value;
    }

    private  Serializable toDateFromString(String value, String format) {
        Date date = DateUtils.guessDate(value, format);

        if (date == null) {
            return value;
        } else {
            return date;
        }
    }

    private static Serializable toNumberFromString(Field field, String value) {
        NumberType numberType = NumberType.guessType(field);
        if (numberType.equals(NumberType.INTEGER)) {
            return Long.parseLong(value);
        } else {
            return Float.parseFloat(value);
        }
    }

    private  Serializable toBooleanFromString(Field field, String value) {
        final TrueFalse annotation = com.elepy.utils.Annotations.get(field, TrueFalse.class);

        if (annotation != null && value.equalsIgnoreCase(annotation.trueValue())) {
            return true;
        }
        return Boolean.parseBoolean(value);
    }
}
