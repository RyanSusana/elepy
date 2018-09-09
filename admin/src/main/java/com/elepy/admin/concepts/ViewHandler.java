package com.elepy.admin.concepts;

import com.elepy.admin.ElepyAdminPanel;
import com.elepy.admin.annotations.View;
import com.elepy.utils.ClassUtils;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.*;

public class ViewHandler {
    private List<Map<String, Object>> descriptors;
    private Map<Class<?>, Map<String, Object>> descriptorMap;
    private Map<ResourceView, Map<String, Object>> customViews;
    private ElepyAdminPanel adminPanel;

    public ViewHandler(ElepyAdminPanel adminPanel) {
        this.adminPanel = adminPanel;


    }


    public void setup() throws ClassNotFoundException, IllegalAccessException, InstantiationException, InvocationTargetException {


        this.descriptors = adminPanel.elepy().getDescriptors();
        this.descriptorMap = mapDescriptors();
        this.customViews = customViews();

        for (ResourceView resourceView : customViews.keySet()) {
            resourceView.setup(adminPanel);
        }
    }

    public void routes() {
        for (Map<String, Object> descriptor : descriptors) {
            adminPanel.http().get("/admin/config" + descriptor.get("slug"), (request, response) -> {
                response.type("application/json");
                return adminPanel.elepy().getObjectMapper().writeValueAsString(
                        descriptor
                );
            });
        }
        for (ResourceView resourceView : customViews.keySet()) {
            adminPanel.http().get("/admin" + resourceView.getDescriptor().get("slug"), (request, response) -> {

                Map<String, Object> model = new HashMap<>();

                model.put("content", resourceView.renderView(resourceView.getDescriptor()));
                model.put("headers", resourceView.renderHeaders());

                model.put("currentDescriptor", resourceView.getDescriptor());
                return adminPanel.renderWithDefaults(request, model, "admin-templates/custom-model.peb");
            });
        }
        for (Map<String, Object> descriptor : descriptors) {
            defaultDecriptorPanel(descriptor, descriptors);
        }

    }

    private Map<Class<?>, Map<String, Object>> mapDescriptors() throws ClassNotFoundException {
        Map<Class<?>, Map<String, Object>> descriptorMap = new HashMap<>();
        for (Map<String, Object> descriptor : descriptors) {
            final Class<?> javaClass = Class.forName((String) descriptor.get("javaClass"));

            descriptorMap.put(javaClass, descriptor);
        }

        return descriptorMap;
    }

    private Map<ResourceView, Map<String, Object>> customViews() throws IllegalAccessException, InvocationTargetException, InstantiationException {
        Map<ResourceView, Map<String, Object>> views = new HashMap<>();
        List<Class<?>> classes = new ArrayList<>();
        for (Class<?> cls : descriptorMap.keySet()) {
            if (cls.isAnnotationPresent(View.class)) {
                Map<ResourceView, Map<String, Object>> map = new HashMap<>();

                final View annotation = cls.getAnnotation(View.class);

                final Optional<Constructor<?>> emptyConstructor = ClassUtils.getEmptyConstructor(annotation.value());

                final ResourceView resourceView = (ResourceView) emptyConstructor.get().newInstance();
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

    private void defaultDecriptorPanel(Map<String, Object> descriptor, List<Map<String, Object>> descriptors) {

        adminPanel.http().get("/admin" + descriptor.get("slug"), (request, response) -> {

            Map<String, Object> model = new HashMap<>();

            model.put("descriptors", descriptors);
            model.put("currentDescriptor", descriptor);
            return adminPanel.renderWithDefaults(request, model, "admin-templates/model.peb");
        });
    }

    public List<Map<String, Object>> getDescriptors() {
        return descriptors;
    }
}
