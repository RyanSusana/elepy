package com.elepy.admin;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class AdminPanelBuilder {


    private String version;

    private List<String> requiredPermissions;

    private static final Logger logger = LoggerFactory.getLogger(AdminPanel.class);


    public AdminPanelBuilder withLocal() {
        this.version = "local";
        return this;
    }

    public AdminPanelBuilder withLatestCDN() {
        this.version = "latest";
        return this;
    }

    public AdminPanelBuilder withCDNVersion(String version) {
        this.version = version;
        return this;
    }

    public AdminPanelBuilder withRequiredPermissions(String... requiredPermissions) {
        return withRequiredPermissions(List.of(requiredPermissions));
    }

    public AdminPanelBuilder withRequiredPermissions(List<String> requiredPermissions) {
        this.requiredPermissions = requiredPermissions;
        return this;
    }


    public AdminPanel build() {
        return new AdminPanel(version, requiredPermissions == null ? "" : String.join(",", requiredPermissions));
    }


}
