package com.elepy.admin.concepts;

import com.elepy.ElepyPostConfiguration;
import com.elepy.admin.ElepyAdminPanel;
import com.elepy.http.HttpService;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PluginHandler {

    private final ElepyAdminPanel adminPanel;
    private final List<ElepyAdminPanelPlugin> plugins;
    private final HttpService http;

    public PluginHandler(ElepyAdminPanel adminPanel, HttpService http) {
        this.adminPanel = adminPanel;
        this.http = http;
        this.plugins = new ArrayList<>();
    }

    public void setupPlugins(ElepyPostConfiguration elepyPostConfiguration) {
        for (ElepyAdminPanelPlugin plugin : this.plugins) {
            plugin.setup(http, elepyPostConfiguration);
        }
    }


    public void setupRoutes(ElepyPostConfiguration elepyPostConfiguration) {
        http.before("/plugins/*", ctx -> elepyPostConfiguration.getAllAdminFilters().authenticate(ctx));
        http.before("/plugins/*/*", ctx -> elepyPostConfiguration.getAllAdminFilters().authenticate(ctx));
        for (ElepyAdminPanelPlugin plugin : this.plugins) {
            plugin.setup(http, elepyPostConfiguration);
            http.get("/plugins/" + plugin.getSlug(), (request, response) -> {
                Map<String, Object> model = new HashMap<>();
                String content = plugin.renderContent(model);
                model.put("content", content);
                model.put("plugin", plugin);
                response.result(adminPanel.renderWithDefaults(request, model, "admin-templates/plugin.peb"));
            });
        }
    }

    public ElepyAdminPanel addPlugin(ElepyAdminPanelPlugin plugin) {
        if (adminPanel.isInitiated()) {
            throw new IllegalStateException("Can't add plugins after before() has been called!");
        }
        plugin.setAdminPanel(adminPanel);
        plugins.add(plugin);
        return adminPanel;
    }

    public ElepyAdminPanel getAdminPanel() {
        return adminPanel;
    }

    public List<ElepyAdminPanelPlugin> getPlugins() {
        return plugins;
    }
}
