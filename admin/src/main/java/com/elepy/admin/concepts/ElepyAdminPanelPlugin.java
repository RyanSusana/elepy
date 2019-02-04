package com.elepy.admin.concepts;

import com.elepy.ElepyPostConfiguration;
import com.elepy.admin.ElepyAdminPanel;
import spark.Service;

import java.util.Map;
import java.util.Objects;

public abstract class ElepyAdminPanelPlugin implements Comparable<ElepyAdminPanelPlugin> {

    private final String name;
    private final String slug;


    private ElepyAdminPanel adminPanel;


    public ElepyAdminPanelPlugin(String name, String slug) {
        this.name = name;
        this.slug = slug;
    }


    public abstract void setup(Service http, ElepyPostConfiguration elepy);

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
    public int compareTo(ElepyAdminPanelPlugin o) {
        return name.compareTo(o.name);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ElepyAdminPanelPlugin that = (ElepyAdminPanelPlugin) o;
        return Objects.equals(name, that.name) &&
                Objects.equals(slug, that.slug);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, slug);
    }
}
