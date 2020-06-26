package com.elepy.utils;

import com.elepy.annotations.ElepyAnnotationsInside;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.util.ArrayList;
import java.util.List;

public interface Annotations {

    static <A> List<A> getAll(AnnotatedElement element, Class<A> aClass) {

        var all = new ArrayList<A>();
        var root = element.getAnnotations();

        for (Annotation annotation : root) {
            if (annotation.annotationType().isAnnotationPresent(ElepyAnnotationsInside.class)) {
                all.addAll(getAll(annotation.annotationType(), aClass));
            }
            if (annotation.annotationType().equals(aClass)) {
                all.add((A) annotation);
            }
        }
        return all;
    }

    static <A extends Annotation> A get(AnnotatedElement element, Class<A> aClass) {

        final var annotation = element.getAnnotation(aClass);

        if (annotation != null) {
            return annotation;
        } else {
            final var annotations = getAll(element, aClass);
            if (annotations.size() > 0)
                return annotations.get(0);
            else
                return null;
        }
    }

    static boolean isPresent(AnnotatedElement property, Class<? extends Annotation> annotationClass) {
        return getAll(property, annotationClass).size() > 0;
    }
}
