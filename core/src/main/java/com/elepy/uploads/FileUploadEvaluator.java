package com.elepy.uploads;

import com.elepy.exceptions.ElepyException;
import com.elepy.http.Request;

public class FileUploadEvaluator {

    private static final long KILOBYTE_SIZE = 1024;
    private static final long MEGABYTE_SIZE = KILOBYTE_SIZE * 1024;

    private final long maxSize;
    private final String requiredContentType;

    public FileUploadEvaluator(Long maxSize, String requiredContentType) {
        this.maxSize = maxSize == null ? 10 * MEGABYTE_SIZE : maxSize;
        this.requiredContentType = requiredContentType == null ? "*/*" : requiredContentType;
    }

    public static FileUploadEvaluator fromRequest(Request request) {
        try {
            final String maxSize = request.queryParams("maxSize");
            return new FileUploadEvaluator(maxSize == null ? null : Long.parseLong(maxSize), request.queryParams("requiredContentType"));
        } catch (NumberFormatException e) {
            throw new ElepyException("maxSize must be a number");
        }
    }

    public void evaluate(FileUpload file) {
        if (!file.contentTypeMatches(requiredContentType)) {
            throw new ElepyException(String.format("Content type must match %s, was %s", requiredContentType, file.getContentType()), 400);
        }

        if (file.getSize() > maxSize) {
            throw new ElepyException(String.format("File size can't exceed %s, was %s",
                    translateToRepresentableString(maxSize),
                    translateToRepresentableString(file.getSize())));
        }
    }

    private String translateToRepresentableString(long bytes) {
        if (maxSize > MEGABYTE_SIZE) {
            return String.format("%d MB", bytes / MEGABYTE_SIZE);
        }
        if (maxSize > KILOBYTE_SIZE) {
            return String.format("%d KB", bytes / KILOBYTE_SIZE);
        } else {
            return String.format("%d bytes", bytes);
        }
    }
}
