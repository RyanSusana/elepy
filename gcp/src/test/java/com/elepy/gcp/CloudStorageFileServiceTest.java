package com.elepy.gcp;

import com.elepy.configuration.Configuration;
import com.elepy.mongo.MongoConfiguration;
import com.elepy.tests.upload.FileServiceTest;
import com.elepy.uploads.FileService;
import com.google.cloud.storage.Storage;
import com.google.cloud.storage.contrib.nio.testing.LocalStorageHelper;

public class CloudStorageFileServiceTest extends FileServiceTest {
    @Override
    public Configuration databaseConfiguration() {
        return MongoConfiguration.inMemory("test", null);
    }

    @Override
    public FileService fileService() {
        final Storage service = LocalStorageHelper.getOptions().getService();
        return new CloudStorageFileService(service, "test");
    }
}
