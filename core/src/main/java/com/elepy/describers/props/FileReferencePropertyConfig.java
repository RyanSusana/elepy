package com.elepy.describers.props;

import com.elepy.annotations.FileReference;
import com.elepy.describers.Property;
import com.elepy.models.FieldType;
import com.elepy.uploads.FileUploadEvaluator;

import java.lang.reflect.AccessibleObject;

public class FileReferencePropertyConfig implements PropertyConfig {

    private final String allowedExtensions;
    private final long maximumFileSize;

    public FileReferencePropertyConfig(String allowedMimeType, long maximumFileSize) {
        this.allowedExtensions = allowedMimeType;
        this.maximumFileSize = maximumFileSize;
    }

    public static FileReferencePropertyConfig of(AccessibleObject accessibleObject) {
        final var annotation = accessibleObject.getAnnotation(FileReference.class);

        return new FileReferencePropertyConfig(annotation == null ? "*/*" : annotation.allowedMimeType(), annotation == null ? FileUploadEvaluator.DEFAULT_MAX_FILE_SIZE : annotation.maximumFileSize());
    }

    public static FileReferencePropertyConfig of(Property property) {
        return new FileReferencePropertyConfig(property.getExtra("allowedMimeType"), property.getExtra("maximumFileSize"));
    }

    @Override
    public void config(Property property) {
        property.setType(FieldType.FILE_REFERENCE);
        property.setExtra("allowedMimeType", allowedExtensions);
        property.setExtra("maximumFileSize", maximumFileSize);
    }

    public String getAllowedMimeType() {
        return allowedExtensions;
    }

    public long getMaxSizeInBytes() {
        return maximumFileSize;
    }
}
