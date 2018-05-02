package com.ryansusana.elepy.admin;

import java.util.Map;

public abstract class ElepyAdminPanelPlugin {

    private final String name;
    private final String slug;
    private final ElepyAdminPanel adminPanel;


    public ElepyAdminPanelPlugin(String name, String slug, ElepyAdminPanel adminPanel) {
        this.name = name;
        this.slug = slug;
        this.adminPanel = adminPanel;
    }


    public abstract String renderContent(Map<String, Object> model);


    public String getName() {
        return name;
    }

    public String getSlug() {
        return slug;
    }
}
