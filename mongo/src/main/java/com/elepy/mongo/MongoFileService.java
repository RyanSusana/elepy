package com.elepy.mongo;

import com.elepy.exceptions.ElepyException;
import com.elepy.uploads.FileService;
import com.elepy.uploads.RawFile;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.gridfs.GridFSBucket;
import com.mongodb.client.gridfs.GridFSBuckets;
import com.mongodb.client.gridfs.model.GridFSFile;
import com.mongodb.client.gridfs.model.GridFSUploadOptions;
import com.mongodb.client.model.Filters;
import org.bson.Document;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public class MongoFileService implements FileService {
    private final GridFSBucket bucket;


    public MongoFileService(MongoDatabase mongoDatabase, String bucket) {
        this.bucket = GridFSBuckets.create(mongoDatabase, bucket == null ? "fs" : bucket);
    }

    @Override
    public void uploadFile(RawFile file) {
        bucket.uploadFromStream(file.getName(), file.getContent(), new GridFSUploadOptions().metadata(new Document().append("contentType", file.getContentType())));
    }

    @Override
    public Optional<RawFile> readFile(String path) {
        try {
            final var file = findByName(path).orElseThrow();

            final ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

            bucket.downloadToStream(file.getObjectId(), outputStream);

            final ByteArrayInputStream inputStream = new ByteArrayInputStream(outputStream.toByteArray());

            outputStream.close();
            inputStream.close();
            return Optional.of(RawFile.of(file.getFilename(), file.getMetadata().getString("contentType"), inputStream, file.getLength()));
        } catch (NoSuchElementException e) {
            return Optional.empty();
        } catch (Exception e) {
            e.printStackTrace();
            return Optional.empty();
        }
    }

    @Override
    public List<String> listFiles() {
        return StreamSupport.stream(bucket.find().spliterator(), false)
                .map(GridFSFile::getFilename)
                .collect(Collectors.toList());
    }

    @Override
    public void deleteFile(String path) {
        var file = findByName(path).orElseThrow(() -> ElepyException.notFound(path)).getObjectId();

        bucket.delete(file);
    }

    private Optional<GridFSFile> findByName(String name) {
        return Optional.ofNullable(bucket.find(Filters.eq("filename", name)).first());
    }
}
