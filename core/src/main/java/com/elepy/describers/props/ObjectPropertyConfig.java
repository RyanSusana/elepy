package com.elepy.describers.props;

import com.elepy.describers.Property;
import com.elepy.describers.StructureDescriber;
import com.elepy.models.FieldType;
import com.elepy.utils.ReflectionUtils;

import java.lang.reflect.AccessibleObject;
import java.util.Set;

public class ObjectPropertyConfig implements PropertyConfig {
    private final String objectName;
    private final Set<Property> properties;

    public ObjectPropertyConfig(String objectName, Set<Property> properties) {
        this.objectName = objectName;
        this.properties = properties;
    }

    public static ObjectPropertyConfig of(AccessibleObject field) {
        Class<?> objectType = ReflectionUtils.returnType(field);
        return new ObjectPropertyConfig(objectType.getSimpleName(), StructureDescriber.describeClass(objectType));
    }

    public static ObjectPropertyConfig of(Property property) {
        return new ObjectPropertyConfig(property.getExtra("objectName"), property.getExtra("properties"));
    }


    @Override
    public void config(Property property) {
        property.setType(FieldType.OBJECT);
        property.setExtra("objectName", objectName);
        property.setExtra("properties", properties);
    }
}
