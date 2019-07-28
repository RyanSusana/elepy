package com.elepy.uploads;

import com.elepy.annotations.Inject;
import com.elepy.dao.Crud;
import com.elepy.describers.ModelContext;
import com.elepy.exceptions.Message;
import com.elepy.http.HttpContext;
import com.elepy.routes.DeleteHandler;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.Serializable;
import java.util.Set;

public class FileReferenceDelete implements DeleteHandler<FileReference> {

    @Inject
    private FileService fileService;

    @Override
    public void handleDelete(HttpContext context, Crud<FileReference> dao, ModelContext<FileReference> modelContext, ObjectMapper objectMapper) throws Exception {
        Set<Serializable> paramIds = context.modelIds();

        dao.getByIds(paramIds).forEach(fileReference -> {
            fileService.deleteFile(fileReference.getUploadName());
            dao.deleteById(fileReference.getUID());
        });

        context.result(Message.of("Successfully deleted items", 200));
    }

}
