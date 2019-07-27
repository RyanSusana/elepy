package com.elepy.uploads;

import java.util.List;
import java.util.Optional;

public interface FileService {
    void uploadFile(FileUpload file);

    Optional<FileUpload> readFile(String path);

    List<String> listFiles();

    void deleteFile(String path);

}
