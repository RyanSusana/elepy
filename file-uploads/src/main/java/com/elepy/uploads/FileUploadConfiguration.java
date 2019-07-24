package com.elepy.uploads;

import com.elepy.Configuration;
import com.elepy.ElepyPostConfiguration;
import com.elepy.ElepyPreConfiguration;

public class FileUploadConfiguration implements Configuration {
    private final DirectoryFileService directoryFileService;

    public FileUploadConfiguration(String rootFolderLocation) {
        directoryFileService = new DirectoryFileService(rootFolderLocation);
    }

    public static FileUploadConfiguration of(String rootFolder) {
        return new FileUploadConfiguration(rootFolder);
    }

    @Override
    public void before(ElepyPreConfiguration elepy) {
        elepy.withUploads(directoryFileService);
    }

    @Override
    public void after(ElepyPostConfiguration elepy) {

    }
}
