package com.elepy.gcp;

import com.elepy.configuration.Configuration;
import com.elepy.configuration.ElepyPostConfiguration;
import com.elepy.configuration.ElepyPreConfiguration;
import com.google.cloud.storage.Storage;
import com.google.cloud.storage.StorageOptions;

public class CloudStorageConfiguration implements Configuration {

    private final String bucket;
    private final Storage storage;

    public CloudStorageConfiguration(Storage storage, String bucket) {
        this.storage = storage;
        this.bucket = bucket;
    }

    public static CloudStorageConfiguration of(String bucketName) {
        return of(StorageOptions.getDefaultInstance().getService(), bucketName);
    }

    public static CloudStorageConfiguration of(Storage storage, String bucketName) {
        return new CloudStorageConfiguration(storage, bucketName);
    }

    @Override
    public void preConfig(ElepyPreConfiguration elepy) {
        elepy.withUploads(new CloudStorageFileService(storage, bucket));
    }

    @Override
    public void postConfig(ElepyPostConfiguration elepy) {

    }
}
