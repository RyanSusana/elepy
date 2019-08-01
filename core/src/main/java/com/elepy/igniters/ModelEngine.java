package com.elepy.igniters;

import com.elepy.Elepy;
import com.elepy.auth.Permissions;
import com.elepy.exceptions.ElepyConfigException;
import com.elepy.exceptions.ElepyException;
import com.elepy.http.HttpMethod;
import com.elepy.http.HttpService;
import com.elepy.http.Route;
import com.elepy.models.Model;
import com.elepy.models.ModelChange;
import com.elepy.utils.ModelUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.elepy.http.RouteBuilder.anElepyRoute;

public class ModelEngine {


    private Elepy elepy;
    private List<ModelPiston<?>> pistons;

    private Map<Class<?>, ModelChange> changesToImplement;

    public ModelEngine(Elepy elepy) {
        this.elepy = elepy;
        this.changesToImplement = new HashMap<>();
        this.pistons = new ArrayList<>();
        setupDescriptors(elepy.getConfigSlug(), elepy.http());
        executeChanges();
    }

    public List<Model<?>> getModels() {
        return pistons.stream().map(ModelPiston::getModel).collect(Collectors.toList());
    }

    public void addModel(Class<?> modelType) {
        final Model<?> modelFromClass = ModelUtils.createModelFromClass(modelType);
        pistons.add(new ModelPiston<>(modelFromClass, elepy));
    }

    public <T> void alterModel(Class<T> cls, ModelChange modelChange) {
        changesToImplement.put(cls, modelChange);
    }

    private void executeChanges() {
        this.changesToImplement.forEach((cls, modelChange) -> pistons
                .stream()
                .filter(modelContext -> modelContext.getModel().getJavaClass().equals(cls))
                .findFirst()
                .orElseThrow(() -> new ElepyConfigException(String.format("No model found with the class: %s", cls.getName())))
                .getModelContext()
                .changeModel(modelChange));
    }

    public void setupDescriptors(String configSlug, HttpService http) {

        final Route build = anElepyRoute()
                .path(configSlug)
                .addPermissions(Permissions.LOGGED_IN)
                .method(HttpMethod.GET)

                .route(ctx -> {
                    ctx.type("application/json");

                    ctx.requirePermissions(Permissions.LOGGED_IN);

                    ctx.response().json(pistons.stream().map(ModelPiston::getModel).collect(Collectors.toList()));

                })
                .build();

        http.addRoute(build);

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
