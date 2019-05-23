package com.elepy.igniters;

import com.elepy.Elepy;
import com.elepy.auth.Permissions;
import com.elepy.describers.Model;
import com.elepy.describers.ModelChange;
import com.elepy.exceptions.ElepyConfigException;
import com.elepy.exceptions.ElepyException;
import com.elepy.http.HttpService;
import com.elepy.utils.ModelUtils;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class ModelEngine {


    private Elepy elepy;
    private List<ModelPiston<?>> pistons;

    public ModelEngine(Elepy elepy) {
        this.elepy = elepy;
        this.pistons = new ArrayList<>();
        setupDescriptors(elepy.getConfigSlug(), elepy.http());
    }

    public List<Model<?>> getModels() {
        return pistons.stream().map(ModelPiston::getModel).collect(Collectors.toList());
    }

    public void addModel(Class<?> modelType) {
        final Model<?> modelFromClass = ModelUtils.createModelFromClass(modelType);
        pistons.add(new ModelPiston<>(modelFromClass, elepy));
    }

    private <T> void alterModel(Class<T> cls, ModelChange modelChange) {
        pistons
                .stream()
                .filter(modelContext -> modelContext.getModel().getJavaClass().equals(cls))
                .findFirst()
                .orElseThrow(() -> new ElepyConfigException(String.format("No model found with the class: %s", cls.getName())))
                .getModelContext()
                .changeModel(modelChange);
    }

    public void setupDescriptors(String configSlug, HttpService http) {
        http.get(configSlug, ctx -> {
            ctx.type("application/json");
            ctx.requirePermissions(Permissions.LOGGED_IN);

            // TODO make result be able to handle regular objects
            ctx.result(new ObjectMapper().writeValueAsString(pistons.stream().map(ModelPiston::getModel).collect(Collectors.toList())));
        });
    }

    public <T> Model<T> getModelForClass(Class<T> clazz) {
        return (Model<T>)
                pistons.stream()
                        .map(ModelPiston::getModel)
                        .filter(model -> model.getJavaClass().equals(clazz))
                        .findFirst()
                        .orElseThrow(() -> new ElepyException(String.format("Can not find model: %s", clazz.getName())));
    }
}
