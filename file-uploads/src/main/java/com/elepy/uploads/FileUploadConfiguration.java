package com.elepy.uploads;

import com.elepy.Configuration;
import com.elepy.ElepyPostConfiguration;
import com.elepy.ElepyPreConfiguration;

public class FileUploadConfiguration implements Configuration {
    private final String rootFolderLocation;

    public FileUploadConfiguration(String rootFolderLocation) {
        this.rootFolderLocation = rootFolderLocation;
    }

    @Override
    public void before(ElepyPreConfiguration elepy) {
        final DirectoryFileService directoryFileService = new DirectoryFileService(rootFolderLocation);
        elepy.withUploads(directoryFileService);

    }

    @Override
    public void after(ElepyPostConfiguration elepy) {

    }
}
