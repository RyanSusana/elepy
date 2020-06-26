package com.elepy.models.options;

import com.elepy.annotations.Custom;
import com.elepy.exceptions.ElepyConfigException;

import java.lang.reflect.AnnotatedElement;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class CustomOptions implements Options {
    private final String umdLocation;
    private final Map<String, String> props;

    public CustomOptions(String umdLocation, Map<String, String> props) {
        this.umdLocation = umdLocation;
        this.props = props;
    }


    public static CustomOptions of(AnnotatedElement field) {
        final var annotation = com.elepy.utils.Annotations.get(field,Custom.class);

        final var map = new HashMap<String, String>();
        Arrays.stream(annotation.props()).forEach(prop -> {
            if (prop.value().length != 2) {
                throw new ElepyConfigException("Prop value length must be 2, found " + prop.value().length);
            }
            map.put(prop.value()[0], prop.value()[1]);
        });
        return new CustomOptions(annotation.umdLocation(), map);
    }

    public String getUmdLocation() {
        return umdLocation;
    }

    public Map<String, String> getProps() {
        return props;
    }
}
