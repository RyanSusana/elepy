package com.elepy.admin.views;

import com.elepy.annotations.ElepyConstructor;
import com.elepy.annotations.Inject;

public class FileView extends OfficialView {
    @ElepyConstructor
    public FileView(@Inject ElepyResourceLocation resourceLocation) {
        super("elepy-file", resourceLocation);
    }

}
