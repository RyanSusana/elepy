package com.elepy.gcp;

import com.elepy.exceptions.ElepyException;
import com.elepy.uploads.FileService;
import com.elepy.uploads.RawFile;
import com.google.cloud.storage.Blob;
import com.google.cloud.storage.BlobId;
import com.google.cloud.storage.BlobInfo;
import com.google.cloud.storage.Storage;

import java.nio.channels.Channels;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public class CloudStorageFileService implements FileService {

    private final String bucket;
    private final Storage storage;


    public CloudStorageFileService(Storage storage, String bucket) {
        this.storage = storage;
        this.bucket = bucket;
    }

    @Override
    public void uploadFile(RawFile file) {
        BlobId blobId = BlobId.of(bucket, file.getName());
        BlobInfo blobInfo = BlobInfo.newBuilder(blobId).setContentType(file.getContentType()).build();
        try {
            storage.create(blobInfo, file.getContent().readAllBytes());
        } catch (Exception e) {
            throw ElepyException.internalServerError(e);
        }
    }

    @Override
    public Optional<RawFile> readFile(String path) {
        Blob blob = storage.get(BlobId.of(bucket, path));

        if (blob != null) {
            return Optional.of(RawFile.of(blob.getName(), blob.getContentType(), Channels.newInputStream(blob.reader()), blob.getSize()));
        } else {
            return Optional.empty();
        }
    }


    @Override
    public List<String> listFiles() {
        return StreamSupport
                .stream(storage.list(bucket).iterateAll().spliterator(), false)
                .map(BlobInfo::getName)
                .collect(Collectors.toList());
    }

    @Override
    public void deleteFile(String path) {
        storage.delete(BlobId.of(bucket, path));
    }
}
