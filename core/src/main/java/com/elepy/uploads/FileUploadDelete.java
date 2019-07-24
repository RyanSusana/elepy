package com.elepy.uploads;

import com.elepy.annotations.Inject;
import com.elepy.dao.Crud;
import com.elepy.routes.SimpleDelete;

public class FileUploadDelete extends SimpleDelete<FileUpload> {

    @Inject
    private FileService fileService;

    @Override
    public void afterDelete(FileUpload itemToDelete, Crud<FileUpload> dao) {
    }

    @Override
    public void beforeDelete(FileUpload deletedItem, Crud<FileUpload> dao) {
        fileService.deleteFile(deletedItem.getName());
    }
}
