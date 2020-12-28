package com.elepy.evaluators;

import com.elepy.utils.ReflectionUtils;
import org.hibernate.validator.spi.nodenameprovider.JavaBeanProperty;
import org.hibernate.validator.spi.nodenameprovider.Property;
import org.hibernate.validator.spi.nodenameprovider.PropertyNodeNameProvider;

public class JsonNodeNameProvider implements PropertyNodeNameProvider {

    @Override
    public String getName(Property property) {
        if (property instanceof JavaBeanProperty) {
            return getJavaBeanPropertyName((JavaBeanProperty) property);
        }

        return getDefaultName(property);
    }

    private String getJavaBeanPropertyName(JavaBeanProperty property) {

        final var propertyField = ReflectionUtils.getPropertyField(property.getDeclaringClass(), property.getName());

        if (propertyField == null) {
            return property.getName();
        }
        return ReflectionUtils.getPropertyName(propertyField);
    }

    private String getDefaultName(Property property) {
        return property.getName();
    }
}
