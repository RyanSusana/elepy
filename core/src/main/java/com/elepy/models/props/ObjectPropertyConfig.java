package com.elepy.models.props;

import com.elepy.annotations.Featured;
import com.elepy.models.FieldType;
import com.elepy.models.Property;
import com.elepy.utils.ModelUtils;
import com.elepy.utils.ReflectionUtils;

import java.lang.reflect.AccessibleObject;
import java.util.List;

public class ObjectPropertyConfig implements PropertyConfig {
    private final String objectName;
    private final String featuredProperty;
    private final List<Property> properties;

    public ObjectPropertyConfig(String objectName, String featuredProperty, List<Property> properties) {
        this.objectName = objectName;
        this.featuredProperty = featuredProperty;
        this.properties = properties;
    }

    public static ObjectPropertyConfig of(AccessibleObject field) {
        Class<?> objectType = ReflectionUtils.returnTypeOf(field);
        return of(objectType);
    }

    public static ObjectPropertyConfig of(Class<?> objectType) {
        final String featuredProperty = ReflectionUtils.searchForFieldWithAnnotation(objectType, Featured.class)
                .map(ReflectionUtils::getPropertyName).orElse(null);

        return new ObjectPropertyConfig(objectType.getSimpleName(), featuredProperty, ModelUtils.describeClass(objectType));
    }

    public static ObjectPropertyConfig of(Property property) {
        return new ObjectPropertyConfig(property.getExtra("objectName"), property.getExtra("featuredProperty"), property.getExtra("properties"));
    }


    @Override
    public void config(Property property) {
        property.setType(FieldType.OBJECT);
        property.setExtra("objectName", objectName);
        property.setExtra("featuredProperty", featuredProperty);
        property.setExtra("properties", properties);
    }

    public String getObjectName() {
        return objectName;
    }

    public String getFeaturedProperty() {
        return featuredProperty;
    }

    public List<Property> getProperties() {
        return properties;
    }
}
