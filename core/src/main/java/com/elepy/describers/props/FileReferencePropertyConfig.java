package com.elepy.describers.props;

import com.elepy.annotations.FileReference;
import com.elepy.describers.Property;
import com.elepy.models.FieldType;

import java.lang.reflect.AccessibleObject;

public class FileReferencePropertyConfig implements PropertyConfig {

    private final String[] allowedExtensions;

    public FileReferencePropertyConfig(String[] allowedExtensions) {
        this.allowedExtensions = allowedExtensions;
    }

    public static FileReferencePropertyConfig of(AccessibleObject accessibleObject) {
        final var annotation = accessibleObject.getAnnotation(FileReference.class);

        return new FileReferencePropertyConfig(annotation == null ? new String[0] : annotation.allowedExtensions());
    }

    public static FileReferencePropertyConfig of(Property property) {
        return new FileReferencePropertyConfig(property.getExtra("allowedExtensions"));
    }

    @Override
    public void config(Property property) {
        property.setType(FieldType.FILE_REFERENCE);
        property.setExtra("allowedExtensions", allowedExtensions);
    }

    public String[] getAllowedExtensions() {
        return allowedExtensions;
    }
}
