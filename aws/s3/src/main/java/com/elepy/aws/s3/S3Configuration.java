package com.elepy.aws.s3;

import com.elepy.configuration.Configuration;
import com.elepy.configuration.ElepyPostConfiguration;
import com.elepy.configuration.ElepyPreConfiguration;
import software.amazon.awssdk.services.s3.S3Client;

public class S3Configuration implements Configuration {
    private final String bucketName;
    private final String prefix;
    private final S3Client s3;

    public S3Configuration(String bucketName, String prefix, S3Client s3) {
        this.bucketName = bucketName;
        this.prefix = prefix;
        this.s3 = s3;
    }

    public static S3Configuration bucket(String bucketName) {
        return new S3Configuration(bucketName, null, S3Client.create());
    }
    public static S3Configuration bucketAndPrefix(String bucketName, String prefix) {
        return new S3Configuration(bucketName, prefix, S3Client.create());
    }



    @Override
    public void preConfig(ElepyPreConfiguration elepy) {
        elepy.withUploads(new S3FileService(
                bucketName,
                prefix,
                s3
        ));
    }

    @Override
    public void postConfig(ElepyPostConfiguration elepy) {

    }
}
