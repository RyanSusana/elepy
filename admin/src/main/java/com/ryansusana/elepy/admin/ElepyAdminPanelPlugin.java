package com.ryansusana.elepy.admin;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mongodb.DB;
import org.jetbrains.annotations.NotNull;
import spark.Service;

import java.util.Map;

public abstract class ElepyAdminPanelPlugin implements Comparable<ElepyAdminPanelPlugin> {

    private final String name;
    private final String slug;


    private ElepyAdminPanel adminPanel;


    public ElepyAdminPanelPlugin(String name, String slug) {
        this.name = name;
        this.slug = slug;
    }


    public abstract void setup(Service http, DB db, ObjectMapper objectMapper);

    public abstract String renderContent(Map<String, Object> model);


    public String getName() {
        return name;
    }

    public String getSlug() {
        return slug;
    }

    public ElepyAdminPanel getAdminPanel() {
        return adminPanel;
    }

    protected void setAdminPanel(ElepyAdminPanel adminPanel) {
        this.adminPanel = adminPanel;
    }

    @Override
    public int compareTo(@NotNull ElepyAdminPanelPlugin o) {
        return name.compareTo(o.name);
    }
}
