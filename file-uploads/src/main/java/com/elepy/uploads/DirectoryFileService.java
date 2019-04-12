package com.elepy.uploads;

import com.elepy.exceptions.ElepyConfigException;
import com.elepy.exceptions.ElepyException;
import com.elepy.http.UploadedFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

public class DirectoryFileService implements FileService {

    private final String rootFolderLocation;

    public DirectoryFileService(String rootFolderLocation) {
        this.rootFolderLocation = rootFolderLocation;
    }


    public void ensureDirsMade() {
        Path path = Paths.get(rootFolderLocation);
        try {
            Files.createDirectories(path);
        } catch (IOException e) {
            throw new ElepyConfigException("Can't create upload folder");
        }
    }

    @Override
    public synchronized void uploadFile(UploadedFile file) {
        try {
            Files.copy(file.getContent(), Paths.get(rootFolderLocation + File.separator + file.getName()));
        } catch (Exception e) {
            throw new ElepyException("Failed to upload file: " + file.getName());
        }
    }

    @Override
    public UploadedFile readFile(String name) {
        final Path path = Paths.get(rootFolderLocation + File.separator + name);

        throw new ElepyException("Failed at retrieving file: " + name, 500);


//        try {
//
//            UploadedFile.of()
//            return Files.newInputStream(path);
//        } catch (IOException e) {
//            throw new ElepyException("Failed at retrieving file: " + name, 500);
//        }
    }

    @Override
    public List<String> listFiles() {
        return null;
    }

}
