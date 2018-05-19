package com.ryansusana.elepy.admin.concepts;

import com.ryansusana.elepy.admin.ElepyAdminPanel;

import java.util.Map;

public interface ResourceView {
    default String renderExtraHeaders(){
        return "";
    }

    void setup(ElepyAdminPanel adminPanel);
    String renderView(Map<String, Object> descriptor);
}
