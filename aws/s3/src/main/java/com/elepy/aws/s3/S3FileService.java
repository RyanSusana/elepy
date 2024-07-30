package com.elepy.aws.s3;

import com.elepy.exceptions.ElepyException;
import com.elepy.uploads.FileService;
import com.elepy.http.RawFile;
import org.slf4j.Logger;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;

import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class S3FileService implements FileService {
    private final String bucketName;
    private final String prefix;
    private final S3Client s3;

    private final static Logger logger = org.slf4j.LoggerFactory.getLogger(S3FileService.class);

    public S3FileService(String bucketName, String prefix, S3Client s3) {
        this.bucketName = bucketName;
        this.prefix = prefix;
        this.s3 = s3;
    }


    @Override
    public void uploadFile(RawFile file) {
        try {
            var metadata = new HashMap<String, String>();
            var putOb = PutObjectRequest.builder()
                    .bucket(bucketName)
                    .key(prefix(file.getName()))
                    .contentType(file.getContentType())
                    .metadata(metadata)
                    .build();

            s3.putObject(putOb, RequestBody.fromInputStream(file.getContent(), file.getSize()));

        } catch (S3Exception e) {
            logger.error("Failed to upload to S3", e);
            throw new ElepyException(String.format("Failed to upload to S3: %s", e.getMessage()), 500, e);
        }
    }

    @Override
    public Optional<RawFile> readFile(String path) {
        try {
            var key = prefix(path);
            var fileRequest = GetObjectRequest.builder()
                    .bucket(bucketName)
                    .key(key)
                    .build();


            var fileResponse = s3.getObject(fileRequest);

            return Optional.of(RawFile.of(path,
                    fileResponse.response().contentType(),
                    fileResponse,
                    fileResponse.response().contentLength()
            ));

        } catch (S3Exception e) {
            if (e.statusCode() == 404) {
                return Optional.empty();
            }
            logger.error("Failed to read from S3", e);
            throw new ElepyException(String.format("Failed to read from S3: %s", e.getMessage()), 500, e);
        }
    }

    @Override
    public List<String> listFiles() {

        try {
            var listObjectsRequest = ListObjectsRequest.builder()
                    .bucket(bucketName)
                    .prefix(prefix)
                    .build();

            var listObjectsResponse = s3.listObjects(listObjectsRequest);

            return listObjectsResponse.contents().stream()
                    .map(s3Object -> unprefix(s3Object.key()))
                    .collect(Collectors.toList());

        } catch (S3Exception e) {
            logger.error("Error listing S3 objects", e);
            throw new ElepyException(String.format("Failed to list from S3: %s", e.getMessage()), e.statusCode(), e);
        }
    }

    @Override
    public void deleteFile(String path) {
        try {
            var deleteObjectRequest = DeleteObjectRequest.builder()
                    .bucket(bucketName)
                    .key(prefix(path))
                    .build();

            s3.deleteObject(deleteObjectRequest);

        } catch (S3Exception e) {
            logger.error("Error deleting S3 object", e);
            throw new ElepyException(String.format("Failed to delete from S3: %s", e.getMessage()), e.statusCode(), e);
        }
    }

    private String unprefix(String path) {
        if (prefix == null) {
            return path;
        }
        return path.replace(prefix + "/", "");
    }
    private String prefix(String path) {
        if (prefix == null) {
            return path;
        }
        return prefix + "/" + path;
    }
}
