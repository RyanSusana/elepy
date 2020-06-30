package com.elepy.models.options;

import com.elepy.annotations.Custom;
import com.elepy.exceptions.ElepyConfigException;
import com.elepy.utils.ReflectionUtils;

import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

public class CustomOptions implements Options, Function<AnnotatedElement, CustomOptions> {
    private final String scriptLocation;
    private final Map<String, ?> props;

    public CustomOptions(String scriptLocation, Map<String, ?> props) {
        this.scriptLocation = scriptLocation;
        this.props = props;
    }

    public static CustomOptions of(AnnotatedElement field) {
        final var annotation = com.elepy.utils.Annotations.get(field, Custom.class);

        assert annotation != null;
        if (CustomOptions.class.equals(annotation.processor())) {
            return getCustomOptionsFromAnnotation(annotation);
        } else {
            return getCustomOptionsFromProcessor(field, annotation.processor());
        }
    }

    private static CustomOptions getCustomOptionsFromAnnotation(Custom annotation) {
        final var map = new HashMap<String, String>();
        Arrays.stream(annotation.props()).forEach(prop -> {
            if (prop.value().length != 2) {
                throw new ElepyConfigException("Prop value length must be 2, found " + prop.value().length);
            }
            map.put(prop.value()[0], prop.value()[1]);
        });
        return new CustomOptions(annotation.scriptLocation(), map);
    }

    private static CustomOptions getCustomOptionsFromProcessor(AnnotatedElement field, Class<? extends Function<AnnotatedElement, CustomOptions>> processorClass) {
        final var emptyConstructor = ReflectionUtils.getEmptyConstructor(processorClass);
        if (emptyConstructor.isEmpty()) {
            throw new ElepyConfigException("Custom annotation processors must have an empty constructor");
        } else {
            try {
                return emptyConstructor.get().newInstance().apply(field);
            } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
                throw new ElepyConfigException("Error instantiationg custom annotation processor", e);
            }
        }
    }

    public String getScriptLocation() {
        return scriptLocation;
    }

    public Map<String, ?> getProps() {
        return props;
    }

    @Override
    public CustomOptions apply(AnnotatedElement field) {
        return of(field);
    }
}
