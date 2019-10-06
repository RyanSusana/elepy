package com.elepy.admin;

import com.elepy.admin.views.CdnResourceLocation;
import com.elepy.admin.views.ResourceLocation;

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
            resourceLocation = new CdnResourceLocation(
                    "https://cdn.jsdelivr.net/npm/elepy-vue@2.1.0/dist/ElepyVue.css",
                    "https://cdn.jsdelivr.net/npm/elepy-vue@2.1.0/dist/ElepyVue.umd.min.js"
            );
        }

        if (elepyAdminPanel == null) {
            elepyAdminPanel = new ElepyAdminPanel();
        }
        return new AdminPanel(elepyAdminPanel, resourceLocation);
    }
} 
