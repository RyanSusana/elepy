package com.elepy.uploads;

import java.util.List;
import java.util.Optional;

public interface FileService {
    void uploadFile(UploadedFile file);

    Optional<UploadedFile> readFile(String name);

    List<String> listFiles();

    void deleteFile(String name);
}
