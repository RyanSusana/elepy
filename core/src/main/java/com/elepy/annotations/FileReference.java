package com.elepy.annotations;

import com.elepy.uploads.FileUploadEvaluator;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD, ElementType.TYPE_USE})
public @interface FileReference {
    String allowedMimeType() default "*/*";

    long maximumFileSize() default FileUploadEvaluator.DEFAULT_MAX_FILE_SIZE;
}
