package com.elepy.admin.concepts;

import com.elepy.ElepyPostConfiguration;
import com.elepy.admin.ElepyAdminPanel;
import com.elepy.admin.annotations.View;
import com.elepy.admin.views.DefaultView;
import com.elepy.describers.Model;
import com.elepy.http.HttpService;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class ViewHandler {


    private final HttpService http;
    private ElepyAdminPanel adminPanel;
    private Map<Model<?>, ModelView> models;

    public ViewHandler(ElepyAdminPanel adminPanel, HttpService http) {
        this.adminPanel = adminPanel;
        this.http = http;
    }


    public void setupModels(ElepyPostConfiguration elepyPostConfiguration) {
        this.models = mapModels(elepyPostConfiguration);
    }


    public void initializeRoutes(ElepyPostConfiguration elepyPostConfiguration) {

        for (Model<?> descriptor : models.keySet()) {
            http.get("/admin/config" + descriptor.getSlug(), (request, response) -> {
                response.type("application/json");
                response.result(elepyPostConfiguration.getObjectMapper().writeValueAsString(
                        descriptor
                ));
            });
        }

        models.forEach((modelDescription, modelView) -> {

            http.get("/admin" + modelDescription.getSlug(), (request, response) -> {

                Map<String, Object> model = new HashMap<>();

                String content = modelView.renderView(request, modelDescription);

                Document document = Jsoup.parse(content);

                Elements styles = document.select("style");
                Elements stylesheets = document.select("stylesheet");

                stylesheets.remove();
                styles.remove();


                model.put("styles", styles);
                model.put("stylesheets", stylesheets.stream().map(sheet -> {
                    if (sheet.hasText()) {
                        return sheet.text();
                    } else if (sheet.hasAttr("src")) {
                        return sheet.attr("src");
                    }
                    return "";
                }).collect(Collectors.toSet()));
                model.put("content", document.body().html());
                model.put("model", modelDescription);
                response.result(adminPanel.renderWithDefaults(request, model, "admin-templates/model.peb"));
            });
        });
    }

    private Map<Model<?>, ModelView> mapModels(ElepyPostConfiguration elepyPostConfiguration) {
        Map<Model<?>, ModelView> modelsToReturn = new HashMap<>();
        for (Model<?> modelContext : elepyPostConfiguration.getModelDescriptions()) {

            if (modelContext.getJavaClass().isAnnotationPresent(View.class)) {

                final View annotation = modelContext.getJavaClass().getAnnotation(View.class);
                final ModelView modelView = elepyPostConfiguration.initializeElepyObject(annotation.value());

                modelsToReturn.put(modelContext, modelView);
            } else {
                modelsToReturn.put(modelContext, elepyPostConfiguration.initializeElepyObject(DefaultView.class));
            }
        }
        return modelsToReturn;
    }


}
