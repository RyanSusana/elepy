package com.elepy.describers;

import com.elepy.annotations.Generated;
import com.elepy.annotations.Hidden;
import com.elepy.annotations.PrettyName;
import com.elepy.exceptions.ElepyConfigException;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

public class ClassDescriber {

    private final Class cls;

    private final List<Map<String, Object>> structure;

    private static final Map<String, String> DATE_FORMATS = new HashMap<>();

    static {
        DATE_FORMATS.put("^\\d{8}$", "yyyyMMdd");
        DATE_FORMATS.put("^\\d{1,2}-\\d{1,2}-\\d{4}$", "dd-MM-yyyy");
        DATE_FORMATS.put("^\\d{4}-\\d{1,2}-\\d{1,2}$", "yyyy-MM-dd");
        DATE_FORMATS.put("^\\d{1,2}/\\d{1,2}/\\d{4}$", "MM/dd/yyyy");
        DATE_FORMATS.put("^\\d{4}/\\d{1,2}/\\d{1,2}$", "yyyy/MM/dd");
        DATE_FORMATS.put("^\\d{1,2}\\s[a-z]{3}\\s\\d{4}$", "dd MMM yyyy");
        DATE_FORMATS.put("^\\d{1,2}\\s[a-z]{4,}\\s\\d{4}$", "dd MMMM yyyy");
        DATE_FORMATS.put("^\\d{12}$", "yyyyMMddHHmm");
        DATE_FORMATS.put("^\\d{8}\\s\\d{4}$", "yyyyMMdd HHmm");
        DATE_FORMATS.put("^\\d{1,2}-\\d{1,2}-\\d{4}\\s\\d{1,2}:\\d{2}$", "dd-MM-yyyy HH:mm");
        DATE_FORMATS.put("^\\d{4}-\\d{1,2}-\\d{1,2}\\s\\d{1,2}:\\d{2}$", "yyyy-MM-dd HH:mm");
        DATE_FORMATS.put("^\\d{1,2}/\\d{1,2}/\\d{4}\\s\\d{1,2}:\\d{2}$", "MM/dd/yyyy HH:mm");
        DATE_FORMATS.put("^\\d{4}/\\d{1,2}/\\d{1,2}\\s\\d{1,2}:\\d{2}$", "yyyy/MM/dd HH:mm");
        DATE_FORMATS.put("^\\d{1,2}\\s[a-z]{3}\\s\\d{4}\\s\\d{1,2}:\\d{2}$", "dd MMM yyyy HH:mm");
        DATE_FORMATS.put("^\\d{1,2}\\s[a-z]{4,}\\s\\d{4}\\s\\d{1,2}:\\d{2}$", "dd MMMM yyyy HH:mm");
        DATE_FORMATS.put("^\\d{14}$", "yyyyMMddHHmmss");
        DATE_FORMATS.put("^\\d{8}\\s\\d{6}$", "yyyyMMdd HHmmss");
        DATE_FORMATS.put("^\\d{1,2}-\\d{1,2}-\\d{4}\\s\\d{1,2}:\\d{2}:\\d{2}$", "dd-MM-yyyy HH:mm:ss");
        DATE_FORMATS.put("^\\d{4}-\\d{1,2}-\\d{1,2}\\s\\d{1,2}:\\d{2}:\\d{2}$", "yyyy-MM-dd HH:mm:ss");
        DATE_FORMATS.put("^\\d{1,2}/\\d{1,2}/\\d{4}\\s\\d{1,2}:\\d{2}:\\d{2}$", "MM/dd/yyyy HH:mm:ss");
        DATE_FORMATS.put("^\\d{4}/\\d{1,2}/\\d{1,2}\\s\\d{1,2}:\\d{2}:\\d{2}$", "yyyy/MM/dd HH:mm:ss");
        DATE_FORMATS.put("^\\d{1,2}\\s[a-z]{3}\\s\\d{4}\\s\\d{1,2}:\\d{2}:\\d{2}$", "dd MMM yyyy HH:mm:ss");
        DATE_FORMATS.put("^\\d{1,2}\\s[a-z]{4,}\\s\\d{4}\\s\\d{1,2}:\\d{2}:\\d{2}$", "dd MMMM yyyy HH:mm:ss");
    }

    public ClassDescriber(Class cls) {
        this.cls = cls;
        this.structure = describe();
    }

    private List<Map<String, Object>> describe() {
        List<Map<String, Object>> fields = new ArrayList<>();

        fields.addAll(describeFields());
        fields.addAll(describeMethods());

        fields.sort((a, b) -> {
            final Integer importanceA = (Integer) a.getOrDefault("importance", 0);
            final Integer importanceB = (Integer) b.getOrDefault("importance", 0);


            return importanceB.compareTo(importanceA);
        });
        return fields;
    }

    private List<Map<String, Object>> describeFields() {
        List<Map<String, Object>> fields = new ArrayList<>();
        for (Field field : cls.getDeclaredFields()) {
            if (!field.isAnnotationPresent(Hidden.class))
                fields.add(new FieldDescriber(field).getFieldMap());
        }

        return fields;

    }

    private List<Map<String, Object>> describeMethods() {
        List<Map<String, Object>> methods = new ArrayList<>();

        for (Method method : cls.getDeclaredMethods()) {
            if (method.isAnnotationPresent(Generated.class)) {
                methods.add(new MethodDescriber(method).getFieldMap());
            }
        }

        return methods;
    }

    public List<Map<String, Object>> getStructure() {
        return structure;
    }

    static List<Map<String, Object>> getEnumMap(Class<?> enumClass) {

        List<Map<String, Object>> toReturn = new ArrayList<>();
        for (Object enumConstant : enumClass.getEnumConstants()) {
            Map<String, Object> toAdd = new HashMap<>();

            toAdd.put("enumValue", enumConstant);

            Field declaredField = null;
            try {
                declaredField = enumClass.getDeclaredField(((Enum) enumConstant).name());
            } catch (NoSuchFieldException ignored) {
                //this exception will never be thrown
            }

            PrettyName annotation = declaredField.getAnnotation(PrettyName.class);
            if (annotation != null) {

                toAdd.put("enumName", annotation.value());
            } else {
                toAdd.put("enumName", ((Enum) enumConstant).name());
            }
            toReturn.add(toAdd);

        }
        return toReturn;
    }

    public static Date guessDate(String string) {
        String dateFormat = guessDateFormat(string);

        if (dateFormat == null) {
            try {
                return new Date(Long.parseLong(string));
            } catch (NumberFormatException ignored) {

            }
        } else {
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat(dateFormat);
            try {
                return simpleDateFormat.parse(string);
            } catch (ParseException ignored) {

            }
        }

        throw new ElepyConfigException(String.format("Can't parse the date '%s'.", string));

    }

    private static String guessDateFormat(String dateString) {
        for (String regexp : DATE_FORMATS.keySet()) {
            if (dateString.toLowerCase().matches(regexp)) {
                return DATE_FORMATS.get(regexp);
            }
        }
        return null;
    }
}
