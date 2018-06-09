package com.elepy.admin.concepts;

import com.elepy.admin.ElepyAdminPanel;

import java.util.Map;

public abstract class ResourceView {

    private Map<String, Object> descriptor;

    public ResourceView() {

    }

    public String renderHeaders() {
        return "";
    }

    public abstract void setup(ElepyAdminPanel adminPanel);

    public abstract String renderView(Map<String, Object> descriptor);


    public Map<String, Object> getDescriptor() {
        return descriptor;
    }

    void setDescriptor(Map<String, Object> descriptor) {
        this.descriptor = descriptor;
    }
}
