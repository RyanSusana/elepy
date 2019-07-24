package com.elepy.uploads;

import com.elepy.annotations.Inject;
import com.elepy.dao.Crud;
import com.elepy.routes.SimpleDelete;

public class FileReferenceDelete extends SimpleDelete<FileReference> {

    @Inject
    private FileService fileService;


    @Override
    public void beforeDelete(FileReference itemToDelete, Crud<FileReference> dao) {
        fileService.deleteFile(itemToDelete.getName());
    }

    @Override
    public void afterDelete(FileReference deletedItem, Crud<FileReference> dao) {

    }

}
