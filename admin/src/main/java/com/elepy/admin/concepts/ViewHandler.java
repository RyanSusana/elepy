package com.elepy.admin.concepts;

import com.elepy.ElepyPostConfiguration;
import com.elepy.admin.ElepyAdminPanel;
import com.elepy.admin.annotations.View;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ViewHandler {
    private List<Map<String, Object>> descriptors;
    private Map<Class<?>, Map<String, Object>> descriptorMap;
    private Map<ResourceView, Map<String, Object>> customViews;
    private ElepyAdminPanel adminPanel;
    private ElepyPostConfiguration elepyPostConfiguration;

    public ViewHandler(ElepyAdminPanel adminPanel) {
        this.adminPanel = adminPanel;
    }


    public void setup(ElepyPostConfiguration elepyPostConfiguration) throws ClassNotFoundException, IllegalAccessException, InstantiationException, InvocationTargetException {


        this.descriptors = elepyPostConfiguration.getDescriptors();
        this.descriptorMap = mapDescriptors();
        this.customViews = customViews();

        this.elepyPostConfiguration = elepyPostConfiguration;
        for (ResourceView resourceView : customViews.keySet()) {
            resourceView.setup(adminPanel);
        }
    }

    public void routes(ElepyPostConfiguration elepyPostConfiguration) {
        for (Map<String, Object> descriptor : descriptors) {
            adminPanel.http().get("/admin/config" + descriptor.get("slug"), (request, response) -> {
                response.type("application/json");
                response.result(elepyPostConfiguration.getObjectMapper().writeValueAsString(
                        descriptor
                ));
            });
        }
        for (ResourceView resourceView : customViews.keySet()) {
            adminPanel.http().get("/admin" + resourceView.getDescriptor().get("slug"), (request, response) -> {

                Map<String, Object> model = new HashMap<>();

                model.put("content", resourceView.renderView(resourceView.getDescriptor()));
                model.put("headers", resourceView.renderHeaders());

                model.put("currentDescriptor", resourceView.getDescriptor());
                response.result(adminPanel.renderWithDefaults(request, model, "admin-templates/custom-model.peb"));
            });
        }
        for (Map<String, Object> descriptor : descriptors) {
            defaultDescriptorPanel(descriptor, descriptors);
        }

    }

    private Map<Class<?>, Map<String, Object>> mapDescriptors() throws ClassNotFoundException {
        Map<Class<?>, Map<String, Object>> newDescriptorMap = new HashMap<>();
        for (Map<String, Object> descriptor : descriptors) {
            final Class<?> javaClass = Class.forName((String) descriptor.get("javaClass"));

            newDescriptorMap.put(javaClass, descriptor);
        }

        return newDescriptorMap;
    }

    private Map<ResourceView, Map<String, Object>> customViews() throws IllegalAccessException, InvocationTargetException, InstantiationException {
        Map<ResourceView, Map<String, Object>> views = new HashMap<>();
        List<Class<?>> classes = new ArrayList<>();

        descriptorMap.forEach((cls, description) -> {

        });
        for (Class<?> cls : descriptorMap.keySet()) {
            if (cls.isAnnotationPresent(View.class)) {

                final View annotation = cls.getAnnotation(View.class);


                final ResourceView resourceView = elepyPostConfiguration.initializeElepyObject(annotation.value());
                resourceView.setDescriptor(descriptorMap.get(cls));
                classes.add(cls);
                views.put(resourceView, descriptorMap.get(cls));
            }
        }
        for (Class<?> cls : classes) {
            descriptorMap.remove(cls);
        }
        return views;
    }

    private void defaultDescriptorPanel(Map<String, Object> descriptor, List<Map<String, Object>> descriptors) {

        adminPanel.http().get("/admin" + descriptor.get("slug"), (request, response) -> {

            Map<String, Object> model = new HashMap<>();

            model.put("descriptors", descriptors);
            model.put("currentDescriptor", descriptor);
            response.result(adminPanel.renderWithDefaults(request, model, "admin-templates/model.peb"));
        });
    }

    public List<Map<String, Object>> getDescriptors() {
        return descriptors;
    }
}
