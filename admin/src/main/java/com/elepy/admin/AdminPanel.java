package com.elepy.admin;

import com.elepy.Configuration;
import com.elepy.ElepyExtension;
import com.elepy.ElepyPostConfiguration;
import com.elepy.ElepyPreConfiguration;
import com.elepy.admin.views.LocalResourceLocation;
import com.elepy.admin.views.ResourceLocation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AdminPanel implements Configuration {

    private final ElepyAdminPanel elepyAdminPanel;
    private static final Logger logger = LoggerFactory.getLogger(AdminPanel.class);

    private final ResourceLocation resourceLocation;

    AdminPanel(ElepyAdminPanel elepyAdminPanel, ResourceLocation location) {
        this.elepyAdminPanel = elepyAdminPanel;
        this.resourceLocation = location;
    }

    public static AdminPanel newAdminPanel() {
        return newBuilder().build();
    }

    public static AdminPanel fromLocalBuild() {
        System.out.println("Local");
        logger.warn("Using the local version of ElepyVue. This might cause unnecessary egress traffic. ");

        System.out.println("local 2");
        return newBuilder().withResourceLocation(new LocalResourceLocation()).build();
    }

    public static AdminPanelBuilder newBuilder() {
        return new AdminPanelBuilder();
    }

    @Override
    public void preConfig(ElepyPreConfiguration elepy) {
        elepy.registerDependency(ResourceLocation.class, resourceLocation);

        if (resourceLocation instanceof LocalResourceLocation) {
            elepy.addExtension((ElepyExtension) resourceLocation);
        }

        elepy.addExtension(elepyAdminPanel);
    }

    @Override
    public void postConfig(ElepyPostConfiguration elepy) {

    }
}
