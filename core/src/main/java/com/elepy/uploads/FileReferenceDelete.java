package com.elepy.uploads;

import com.elepy.annotations.Inject;
import com.elepy.dao.Crud;
import com.elepy.exceptions.Message;
import com.elepy.handlers.DeleteHandler;
import com.elepy.http.HttpContext;
import com.elepy.models.ModelContext;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.Serializable;
import java.util.Set;

public class FileReferenceDelete implements DeleteHandler<FileReference> {

    @Inject
    private FileService fileService;

    @Override
    public void handleDelete(HttpContext context, Crud<FileReference> dao, ModelContext<FileReference> modelContext, ObjectMapper objectMapper) throws Exception {
        Set<Serializable> paramIds = context.recordIds();

        dao.getByIds(paramIds).forEach(fileReference -> {
            fileService.deleteFile(fileReference.getUploadName());
            dao.deleteById(fileReference.getUploadName());
        });

        context.result(Message.of("Successfully deleted items", 200));
    }

}
