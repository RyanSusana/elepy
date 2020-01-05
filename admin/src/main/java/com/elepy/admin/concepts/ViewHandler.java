package com.elepy.admin.concepts;

import com.elepy.ElepyPostConfiguration;
import com.elepy.admin.ElepyAdminPanel;
import com.elepy.admin.views.DefaultView;
import com.elepy.admin.views.FileView;
import com.elepy.annotations.View;
import com.elepy.http.HttpService;
import com.elepy.models.Schema;
import com.elepy.models.ModelView;
import com.elepy.uploads.FileReference;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.stream.Collectors;

public class ViewHandler {


    private final List<Schema<?>> schemas;
    private final HttpService http;
    private ElepyAdminPanel adminPanel;


    public ViewHandler(List<Schema<?>> schemas, ElepyAdminPanel adminPanel, HttpService http) {
        this.adminPanel = adminPanel;
        this.http = http;
        this.schemas = schemas;
    }


    public void setupModels(ElepyPostConfiguration elepy) {
        var models = getModelsFromElepy(elepy);

        models.forEach((elepyModel, modelView) -> {

            http.get("/admin/config" + elepyModel.getPath(), (request, response) -> {
                response.type("application/json");
                response.json(elepyModel);
            });

            http.get("/admin" + elepyModel.getPath(), (request, response) -> {

                Map<String, Object> renderModel = new HashMap<>();

                String content = modelView.renderView(request, elepyModel);

                Document document = Jsoup.parse(content);

                Elements styles = document.select("style");
                Elements stylesheets = document.select("stylesheet");

                stylesheets.remove();
                styles.remove();


                renderModel.put("styles", styles);
                renderModel.put("stylesheets", stylesheets.stream().map(sheet -> {
                    if (sheet.hasText()) {
                        return sheet.text();
                    } else if (sheet.hasAttr("src")) {
                        return sheet.attr("src");
                    }
                    return "";
                }).collect(Collectors.toSet()));
                renderModel.put("content", document.body().html());
                renderModel.put("model", elepyModel);
                renderModel.put("models", models.keySet());
                renderModel.put("properties", request.elepy().getDependency(Properties.class));
                response.result(adminPanel.renderWithDefaults(request,renderModel, "admin-templates/model.peb"));
            });
        });
    }

    private Map<Schema<?>, ModelView> getModelsFromElepy(ElepyPostConfiguration elepyPostConfiguration) {
        Map<Schema<?>, ModelView> modelsToReturn = new HashMap<>();

        schemas.forEach(model -> modelsToReturn.put(model, getViewFromModel(model, elepyPostConfiguration)));

        return modelsToReturn;
    }

    private ModelView getViewFromModel(Schema<?> schema, ElepyPostConfiguration elepyPostConfiguration) {
        if (schema.getJavaClass().equals(FileReference.class)) {
            return elepyPostConfiguration.initializeElepyObject(FileView.class);
        } else if (schema.getJavaClass().isAnnotationPresent(View.class)) {
            final View annotation = schema.getJavaClass().getAnnotation(View.class);
            return elepyPostConfiguration.initializeElepyObject(annotation.value());
        } else {
            return elepyPostConfiguration.initializeElepyObject(DefaultView.class);
        }
    }

}
