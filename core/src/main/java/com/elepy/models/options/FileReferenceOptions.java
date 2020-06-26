package com.elepy.models.options;

import com.elepy.annotations.FileReference;
import com.elepy.uploads.FileUploadEvaluator;

import java.lang.reflect.AnnotatedElement;

public class FileReferenceOptions implements Options {
    private String allowedMimeType;
    private long maximumFileSize;

    public FileReferenceOptions(String allowedMimeType, long maximumFileSize) {
        this.allowedMimeType = allowedMimeType;
        this.maximumFileSize = maximumFileSize;
    }

    public static FileReferenceOptions of(AnnotatedElement accessibleObject){
        final var annotation = com.elepy.utils.Annotations.get(accessibleObject,FileReference.class);

        return new FileReferenceOptions(annotation == null ? "*/*" : annotation.allowedMimeType(), annotation == null ? FileUploadEvaluator.DEFAULT_MAX_FILE_SIZE : annotation.maximumFileSize());

    }

    public String getAllowedMimeType() {
        return allowedMimeType;
    }

    public long getMaximumFileSize() {
        return maximumFileSize;
    }
}
