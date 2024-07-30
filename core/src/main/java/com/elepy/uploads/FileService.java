package com.elepy.uploads;

import com.elepy.http.RawFile;

import java.util.List;
import java.util.Optional;

public interface FileService {
    void uploadFile(RawFile file);

    Optional<RawFile> readFile(String path);

    List<String> listFiles();

    void deleteFile(String path);

}
