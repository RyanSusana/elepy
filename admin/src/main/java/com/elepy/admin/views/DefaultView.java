package com.elepy.admin.views;

import com.elepy.annotations.ElepyConstructor;
import com.elepy.annotations.Inject;

public class DefaultView extends OfficialView {
    @ElepyConstructor
    public DefaultView(@Inject ResourceLocation resourceLocation) {
        super("elepy-default", resourceLocation);
    }
}
