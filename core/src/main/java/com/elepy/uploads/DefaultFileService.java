package com.elepy.uploads;

import com.elepy.exceptions.ElepyException;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class DefaultFileService implements FileService {

    @Override
    public void uploadFile(FileUpload file) {
        error();
    }

    @Override
    public Optional<FileUpload> readFile(String name) {
        error();
        return Optional.empty();
    }

    @Override
    public List<String> listFiles() {
        error();
        return new ArrayList<>();
    }

    @Override
    public void deleteFile(String name) {
        error();
    }

    private void error() {
        throw new ElepyException("No FileService specified, please configure one!");
    }
}
