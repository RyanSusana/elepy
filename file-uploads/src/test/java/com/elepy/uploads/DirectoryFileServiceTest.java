package com.elepy.uploads;

import com.elepy.tests.upload.FileServiceTest;

public class DirectoryFileServiceTest extends FileServiceTest {

    private static final String UPLOAD_DIR = "src/test/resources/uploads";

    @Override
    public FileService fileService() {
        return new DirectoryFileService(UPLOAD_DIR);
    }
}
