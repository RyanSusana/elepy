package com.elepy.concepts.describers;

import com.elepy.annotations.GeneratedField;
import com.elepy.annotations.Hidden;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class StructureDescriber {

    private final Class cls;

    private final List<Map<String, Object>> structure;

    public StructureDescriber(Class cls) {
        this.cls = cls;
        this.structure = describe();
    }

    private List<Map<String, Object>> describe() {
        List<Map<String, Object>> fields = new ArrayList<>();

        fields.addAll(describeFields());
        fields.addAll(describeMethods());
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
        List<Map<String, Object>> fields = new ArrayList<>();
        for (Method field : cls.getMethods()) {
            if (field.isAnnotationPresent(GeneratedField.class))
                fields.add(new MethodDescriber(field).getFieldMap());
        }

        return fields;
    }

    public List<Map<String, Object>> getStructure() {
        return structure;
    }
}
