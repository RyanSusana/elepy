package com.elepy.uploads;

import com.elepy.http.UploadedFile;

import java.util.List;

public interface FileService {
    void uploadFile(UploadedFile file);

    UploadedFile readFile(String name);

    List<String> listFiles();
}
