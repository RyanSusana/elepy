package com.elepy.admin.views;

import com.elepy.annotations.ElepyConstructor;
import com.elepy.annotations.Inject;

public class SingleView extends OfficialView {

    @ElepyConstructor
    public SingleView(@Inject ElepyResourceLocation resourceLocation) {
        super("elepy-single", resourceLocation);
    }
}
