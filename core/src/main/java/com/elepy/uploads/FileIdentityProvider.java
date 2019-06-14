package com.elepy.uploads;

import com.elepy.dao.Crud;
import com.elepy.exceptions.ElepyException;
import com.elepy.id.IdentityProvider;

public class FileIdentityProvider implements IdentityProvider<UploadedFile> {
    @Override
    public void provideId(UploadedFile item, Crud<UploadedFile> dao) {

        dao.getById(item.getName()).ifPresent(file -> {
            throw new ElepyException("There is already a file called: " + item.getName(), 400);
        });
        item.setId(item.getName());
    }
}
