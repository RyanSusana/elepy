package com.elepy.admin.concepts;

import com.elepy.ElepyPostConfiguration;
import com.elepy.admin.ElepyAdminPanel;
import com.elepy.admin.annotations.View;
import com.elepy.describers.ModelContext;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class ViewHandler {


    private ElepyAdminPanel adminPanel;

    private Map<ModelContext<?>, RestModelView> models;

    public ViewHandler(ElepyAdminPanel adminPanel) {
        this.adminPanel = adminPanel;
    }


    public void setupModels(ElepyPostConfiguration elepyPostConfiguration) throws IllegalAccessException, InstantiationException, InvocationTargetException {
        this.models = mapModels(elepyPostConfiguration);
    }


    public void initializeRoutes(ElepyPostConfiguration elepyPostConfiguration) {

        for (ModelContext<?> descriptor : models.keySet()) {
            adminPanel.http().get("/admin/config" + descriptor.getSlug(), (request, response) -> {
                response.type("application/json");
                response.result(elepyPostConfiguration.getObjectMapper().writeValueAsString(
                        descriptor.getJsonDescription()
                ));
            });
        }

        models.forEach((modelDescription, restModelView) -> {
            if (restModelView == null) {

                //Default View
                adminPanel.http().get("/admin" + modelDescription.getSlug(), (request, response) -> {

                    Map<String, Object> model = new HashMap<>();
                    model.put("currentDescriptor", modelDescription.getJsonDescription());
                    response.result(adminPanel.renderWithDefaults(request, model, "admin-templates/model.peb"));
                });
            } else {

                //Custom View
                adminPanel.http().get("/admin" + modelDescription.getSlug(), (request, response) -> {

                    Map<String, Object> model = new HashMap<>();

                    String content = restModelView.renderView(modelDescription);

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
                    model.put("currentDescriptor", modelDescription.getJsonDescription());
                    response.result(adminPanel.renderWithDefaults(request, model, "admin-templates/custom-model.peb"));
                });
            }
        });
    }

    private Map<ModelContext<?>, RestModelView> mapModels(ElepyPostConfiguration elepyPostConfiguration) throws IllegalAccessException, InstantiationException, InvocationTargetException {
        Map<ModelContext<?>, RestModelView> modelsToReturn = new HashMap<>();
        for (ModelContext<?> modelContext : elepyPostConfiguration.getModelDescriptions()) {

            if (modelContext.getModelType().isAnnotationPresent(View.class)) {

                final View annotation = modelContext.getModelType().getAnnotation(View.class);
                final RestModelView restModelView = elepyPostConfiguration.initializeElepyObject(annotation.value());

                modelsToReturn.put(modelContext, restModelView);
            } else {
                modelsToReturn.put(modelContext, null);
            }
        }
        return modelsToReturn;
    }


}
