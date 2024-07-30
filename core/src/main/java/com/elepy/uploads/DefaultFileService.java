package com.elepy.uploads;

import com.elepy.exceptions.ElepyConfigException;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class DefaultFileService implements FileService {

    @Override
    public void uploadFile(RawFile file) {
        error();
    }

    @Override
    public Optional<RawFile> readFile(String path) {
        error();
        return Optional.empty();
    }

    @Override
    public List<String> listFiles() {
        error();
        return new ArrayList<>();
    }

    @Override
    public void deleteFile(String path) {
        error();
    }

    private void error() {
        throw new ElepyConfigException("No FileService specified, please configure one!");
    }
}
