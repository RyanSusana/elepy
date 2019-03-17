package com.elepy.admin.concepts;

import com.elepy.ElepyPostConfiguration;
import com.elepy.admin.ElepyAdminPanel;
import com.elepy.admin.annotations.View;
import com.elepy.describers.ModelDescription;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;

public class ViewHandler {
    private ElepyAdminPanel adminPanel;

    private Map<ModelDescription<?>, RestModelView> models;

    public ViewHandler(ElepyAdminPanel adminPanel) {
        this.adminPanel = adminPanel;
    }


    public void setup(ElepyPostConfiguration elepyPostConfiguration) throws ClassNotFoundException, IllegalAccessException, InstantiationException, InvocationTargetException {

        this.models = mapModels(elepyPostConfiguration);
    }


    public void routes(ElepyPostConfiguration elepyPostConfiguration) {

        for (ModelDescription<?> descriptor : models.keySet()) {
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

                    model.put("content", restModelView.renderView(modelDescription));

                    // TODO parse stylesheets
                    model.put("headers", null);

                    model.put("currentDescriptor", modelDescription.getJsonDescription());
                    response.result(adminPanel.renderWithDefaults(request, model, "admin-templates/custom-model.peb"));
                });
            }
        });
    }

    private Map<ModelDescription<?>, RestModelView> mapModels(ElepyPostConfiguration elepyPostConfiguration) throws IllegalAccessException, InstantiationException, InvocationTargetException {
        Map<ModelDescription<?>, RestModelView> modelsToReturn = new HashMap<>();
        for (ModelDescription<?> modelDescription : elepyPostConfiguration.getModelDescriptions()) {

            if (modelDescription.getModelType().isAnnotationPresent(View.class)) {

                final View annotation = modelDescription.getModelType().getAnnotation(View.class);
                final RestModelView restModelView = elepyPostConfiguration.initializeElepyObject(annotation.value());

                modelsToReturn.put(modelDescription, restModelView);
            } else {
                modelsToReturn.put(modelDescription, null);
            }
        }
        return modelsToReturn;
    }
}
