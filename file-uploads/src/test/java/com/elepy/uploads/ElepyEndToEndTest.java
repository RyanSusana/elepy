package com.elepy.uploads;

import com.elepy.tests.upload.UploadEndToEndTest;

public class ElepyEndToEndTest extends UploadEndToEndTest {

    private static final String UPLOAD_DIR = "src/test/resources/uploads";

    @Override
    public String testName() {
        return "Test local file upload";
    }

    @Override
    public FileService fileService() {
        return new DirectoryFileService(UPLOAD_DIR);
    }
}
