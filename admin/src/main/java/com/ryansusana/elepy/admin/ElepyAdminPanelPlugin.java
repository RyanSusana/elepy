package com.ryansusana.elepy.admin;

import java.util.Map;

public abstract class ElepyAdminPanelPlugin {

    private final String name;
    private final String slug;
    private final ElepyAdminPanel adminPanel;

    protected ElepyAdminPanelPlugin(String name, String slug, ElepyAdminPanel adminPanel) {
        this.name = name;
        this.slug = slug;
        this.adminPanel = adminPanel;
    }


    public abstract String render(Map<String, Object> model);


}
