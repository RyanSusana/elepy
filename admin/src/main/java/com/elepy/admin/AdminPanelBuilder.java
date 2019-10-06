package com.elepy.admin;

import com.elepy.admin.views.CdnResourceLocation;
import com.elepy.admin.views.ResourceLocation;
import com.elepy.exceptions.ElepyConfigException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Objects;
import java.util.Properties;

public class AdminPanelBuilder {
    private ElepyAdminPanel elepyAdminPanel;


    private ResourceLocation resourceLocation;


    private static final Logger logger = LoggerFactory.getLogger(AdminPanel.class);

    public AdminPanelBuilder withResourceLocation(ResourceLocation resourceLocation) {
        this.resourceLocation = resourceLocation;
        return this;
    }

    public AdminPanelBuilder withCDNVersion(String version) {
        return withResourceLocation(CdnResourceLocation.version(version));
    }

    public AdminPanelBuilder withExtension(ElepyAdminPanel elepyAdminPanel) {
        this.elepyAdminPanel = elepyAdminPanel;
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

        if (elepyAdminPanel == null) {
            elepyAdminPanel = new ElepyAdminPanel();
        }
        return new AdminPanel(elepyAdminPanel, resourceLocation);
    }
} 
