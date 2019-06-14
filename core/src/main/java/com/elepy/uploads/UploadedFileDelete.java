package com.elepy.uploads;

import com.elepy.annotations.Inject;
import com.elepy.dao.Crud;
import com.elepy.routes.SimpleDelete;

public class UploadedFileDelete extends SimpleDelete<UploadedFile> {

    @Inject
    private FileService fileService;

    @Override
    public void afterDelete(UploadedFile itemToDelete, Crud<UploadedFile> dao) {
    }

    @Override
    public void beforeDelete(UploadedFile deletedItem, Crud<UploadedFile> dao) {
        fileService.deleteFile(deletedItem.getName());
    }
}
