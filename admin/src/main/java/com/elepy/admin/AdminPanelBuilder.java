package com.elepy.admin;

import com.elepy.admin.views.CdnResourceLocation;
import com.elepy.admin.views.LocalResourceLocation;
import com.elepy.admin.views.ResourceLocation;
import com.elepy.exceptions.ElepyConfigException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.List;
import java.util.Objects;
import java.util.Properties;

public class AdminPanelBuilder {


    private ResourceLocation resourceLocation;

    private List<String> requiredPermissions;

    private static final Logger logger = LoggerFactory.getLogger(AdminPanel.class);

    public AdminPanelBuilder withResourceLocation(ResourceLocation resourceLocation) {
        this.resourceLocation = resourceLocation;
        return this;
    }

    public AdminPanelBuilder withLocal() {
        return withResourceLocation(new LocalResourceLocation());
    }

    public AdminPanelBuilder withLatestCDN() {
        return withResourceLocation(null);
    }

    public AdminPanelBuilder withCDNVersion(String version) {
        return withResourceLocation(CdnResourceLocation.version(version));
    }

    public AdminPanelBuilder withRequiredPermissions(String... requiredPermissions) {
        return withRequiredPermissions(List.of(requiredPermissions));
    }

    public AdminPanelBuilder withRequiredPermissions(List<String> requiredPermissions) {
        this.requiredPermissions = requiredPermissions;
        return this;
    }


    public AdminPanel build() {
        if (resourceLocation == null) {

            final var resourceAsStream = this.getClass().getClassLoader().getResourceAsStream("admin.properties");
            final var properties = new Properties();
            try {
                properties.load(Objects.requireNonNull(resourceAsStream));
            } catch (IOException | NullPointerException e) {
                throw new ElepyConfigException("Error reading Elepy version", e);
            }

            logger.info(String.format("Using ElepyVue version '%s'", properties.getProperty("elepyVersion")));
            resourceLocation = CdnResourceLocation.version(properties.getProperty("elepyVersion"));
        }


        return new AdminPanel(new ElepyAdminPanel(requiredPermissions), resourceLocation);
    }


}
