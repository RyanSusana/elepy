package com.elepy.concepts.describers;

import com.elepy.annotations.Generated;
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
            System.out.println(cls.getName());
            System.out.println(field.getName()+": "+ field.getAnnotations().length);
            if (!field.isAnnotationPresent(Hidden.class))
                fields.add(new FieldDescriber(field).getFieldMap());
        }

        return fields;

    }

    private List<Map<String, Object>> describeMethods() {
        List<Map<String, Object>> methods = new ArrayList<>();

        for (Method method : cls.getDeclaredMethods()) {
            System.out.println(cls.getName());
            System.out.println(method.getName()+": "+ method.getAnnotations().length);
            if (method.isAnnotationPresent(Generated.class)) {
                System.out.println("Method");
                methods.add(new MethodDescriber(method).getFieldMap());
            }
        }

        return methods;
    }

    public List<Map<String, Object>> getStructure() {
        return structure;
    }
}
