package com.elepy.uploads;

import com.elepy.configuration.Configuration;
import com.elepy.configuration.ElepyPostConfiguration;
import com.elepy.configuration.ElepyPreConfiguration;
import com.elepy.annotations.ElepyConstructor;
import com.elepy.annotations.Property;

public class FileUploadConfiguration implements Configuration {
    private final DirectoryFileService directoryFileService;

    @ElepyConstructor
    public FileUploadConfiguration(

            @Property(key = "${uploads.location}") String rootFolderLocation) {
        directoryFileService = new DirectoryFileService(rootFolderLocation);
    }

    public static FileUploadConfiguration of(String rootFolder) {
        return new FileUploadConfiguration(rootFolder);
    }

    @Override
    public void preConfig(ElepyPreConfiguration elepy) {
        elepy.withUploads(directoryFileService);
    }

    @Override
    public void postConfig(ElepyPostConfiguration elepy) {

    }
}
