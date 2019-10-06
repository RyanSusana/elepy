package com.elepy.admin;

import com.elepy.admin.views.CdnResourceLocation;
import com.elepy.admin.views.ResourceLocation;
import com.elepy.exceptions.ElepyConfigException;

import java.io.IOException;
import java.util.Objects;
import java.util.Properties;

public class AdminPanelBuilder {
    private ElepyAdminPanel elepyAdminPanel;


    private ResourceLocation resourceLocation;

    public AdminPanelBuilder withResourceLocation(ResourceLocation resourceLocation) {
        this.resourceLocation = resourceLocation;
        return this;
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

            resourceLocation = new CdnResourceLocation(
                    String.format("https://cdn.jsdelivr.net/npm/elepy-vue@%s/dist/ElepyVue.css", properties.getProperty("version")),
                    String.format("https://cdn.jsdelivr.net/npm/elepy-vue@%s/dist/ElepyVue.umd.min.js", properties.getProperty("version"))
            );
        }

        if (elepyAdminPanel == null) {
            elepyAdminPanel = new ElepyAdminPanel();
        }
        return new AdminPanel(elepyAdminPanel, resourceLocation);
    }
} 
