package com.elepy.uploads;

import com.elepy.annotations.Inject;
import com.elepy.dao.Crud;
import com.elepy.exceptions.Message;
import com.elepy.handlers.ActionHandler;
import com.elepy.http.HttpContext;
import com.elepy.models.ModelContext;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.Serializable;
import java.util.Set;

public class FileReferenceDelete implements ActionHandler<FileReference> {

    @Inject
    private FileService fileService;

    @Override
    public void handle(HttpContext context, ModelContext<FileReference> modelContext) throws Exception {
        Set<Serializable> paramIds = context.recordIds();

        final var crud = modelContext.getCrud();
        crud.getByIds(paramIds).forEach(fileReference -> {
            fileService.deleteFile(fileReference.getUploadName());
            crud.deleteById(fileReference.getUploadName());
        });

        context.result(Message.of("Successfully deleted items", 200));
    }

}
