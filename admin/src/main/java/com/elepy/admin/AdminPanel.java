package com.elepy.admin;

import com.elepy.Configuration;
import com.elepy.ElepyPostConfiguration;
import com.elepy.ElepyPreConfiguration;
import com.elepy.admin.views.ElepyResourceLocation;

public class AdminPanel implements Configuration {

    private final ElepyAdminPanel elepyAdminPanel;


    private ElepyResourceLocation resourceLocation = new ElepyResourceLocation(
            "https://cdn.jsdelivr.net/npm/elepy-vue@2.1.0/dist/ElepyVue.css",
            "https://cdn.jsdelivr.net/npm/elepy-vue@2.1.0/dist/ElepyVue.umd.min.js"
    );

    AdminPanel(ElepyAdminPanel elepyAdminPanel) {
        this.elepyAdminPanel = elepyAdminPanel;
    }


    public static AdminPanel newAdminPanel() {
        return of(new ElepyAdminPanel());
    }

    public static AdminPanel of(ElepyAdminPanel elepyAdminPanel) {
        return new AdminPanel(elepyAdminPanel);
    }

    public static AdminPanel of(AdminPanelConfigurator configurator) {
        final ElepyAdminPanel elepyAdminPanel = new ElepyAdminPanel();
        configurator.configure(elepyAdminPanel);

        return of(elepyAdminPanel);
    }

    @Override
    public void preConfig(ElepyPreConfiguration elepy) {
        elepy.registerDependency(resourceLocation);
        elepy.addExtension(elepyAdminPanel);
    }

    @Override
    public void postConfig(ElepyPostConfiguration elepy) {

    }
}
