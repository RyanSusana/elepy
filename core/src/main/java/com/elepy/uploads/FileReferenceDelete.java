package com.elepy.uploads;

import com.elepy.exceptions.Message;
import com.elepy.handlers.ActionHandler;
import com.elepy.handlers.HandlerContext;
import jakarta.inject.Inject;

import java.io.Serializable;
import java.util.Set;

public class FileReferenceDelete implements ActionHandler<FileReference> {

    @Inject
    private FileService fileService;

    @Override
    public void handle(HandlerContext<FileReference> ctx) throws Exception {
        Set<Serializable> paramIds = ctx.http().recordIds();

        final var crud = ctx.crud();
        crud.getByIds(paramIds).forEach(fileReference -> {
            fileService.deleteFile(fileReference.getUploadName());
            crud.deleteById(fileReference.getUploadName());
        });

        ctx.http().result(Message.of("Successfully deleted items", 200));
    }

}
