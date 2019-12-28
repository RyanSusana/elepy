package com.elepy.admin.concepts;

import com.elepy.ElepyPostConfiguration;
import com.elepy.admin.ElepyAdminPanel;
import com.elepy.http.HttpService;

import java.util.Map;
import java.util.Objects;

public abstract class ElepyAdminPanelPlugin implements Comparable<ElepyAdminPanelPlugin> {

    private final String name;
    private final String path ;


    private ElepyAdminPanel adminPanel;


    public ElepyAdminPanelPlugin(String name, String path ) {
        this.name = name;
        this.path = path ;
    }


    public abstract void setup(HttpService http, ElepyPostConfiguration elepy);

    public abstract String renderContent(Map<String, Object> model);


    public String getName() {
        return name;
    }

    public String getPath() {
        return path ;
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
                Objects.equals(path , that.path );
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, path );
    }
}
