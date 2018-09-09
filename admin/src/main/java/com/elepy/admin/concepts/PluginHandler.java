package com.elepy.admin.concepts;

import com.elepy.admin.ElepyAdminPanel;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mongodb.DB;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PluginHandler {

    private final ElepyAdminPanel adminPanel;
    private final List<ElepyAdminPanelPlugin> plugins;

    public PluginHandler(ElepyAdminPanel adminPanel) {
        this.adminPanel = adminPanel;
        this.plugins = new ArrayList<>();
    }

    public void setupPlugins() {
        for (ElepyAdminPanelPlugin plugin : this.plugins) {
            plugin.setup(adminPanel.http(), adminPanel.elepy().getSingleton(DB.class), new ObjectMapper());
        }

    }


    public void setupRoutes() {

        adminPanel.http().before("/plugins/*", (request, response) -> adminPanel.elepy().allAdminFilters().handle(request, response));
        adminPanel.http().before("/plugins/*/*", (request, response) -> adminPanel.elepy().allAdminFilters().handle(request, response));
        for (ElepyAdminPanelPlugin plugin : this.plugins) {
            plugin.setup(adminPanel.http(), adminPanel.elepy().getDb(), adminPanel.elepy().getObjectMapper());
            adminPanel.http().get("/plugins/" + plugin.getSlug(), (request, response) -> {
                Map<String, Object> model = new HashMap<>();
                String content = plugin.renderContent(null);
                model.put("content", content);
                model.put("plugin", plugin);
                return adminPanel.renderWithDefaults(request, model, "admin-templates/plugin.peb");
            });
        }
    }

    public ElepyAdminPanel addPlugin(ElepyAdminPanelPlugin plugin) {
        if (adminPanel.isInitiated()) {
            throw new IllegalStateException("Can't add plugins after setup() has been called!");
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
