package com.elepy.igniters;

import com.elepy.Elepy;
import com.elepy.auth.permissions.Permissions;
import com.elepy.exceptions.ElepyConfigException;
import com.elepy.exceptions.ElepyException;
import com.elepy.http.HttpMethod;
import com.elepy.http.HttpService;
import com.elepy.http.Route;
import com.elepy.models.ModelChange;
import com.elepy.models.Schema;
import com.elepy.models.SchemaFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.elepy.http.RouteBuilder.anElepyRoute;

public class ModelEngine {

    private final Elepy elepy;
    private final List<ModelPiston<?>> pistons;

    private final SchemaFactory schemaFactory;
    private final Map<Class<?>, ModelChange> changesToImplement;

    public ModelEngine(Elepy elepy) {
        this.elepy = elepy;
        this.changesToImplement = new HashMap<>();
        this.pistons = new ArrayList<>();
        setupDescriptors( elepy.http());
        schemaFactory = new SchemaFactory();
    }

    public void start() {
        pistons.stream()
                .peek(ModelPiston::setupDependencies)
                .collect(Collectors.toSet())
                .forEach(ModelPiston::setupRouting);
    }


    public void addModel(Class<?> modelType) {
        final Schema<?> schemaFromClass = schemaFactory.createDeepSchema(modelType);
        final ModelPiston<?> piston = new ModelPiston<>(schemaFromClass, elepy);
        pistons.add(piston);
    }

    public <T> void alterModel(Class<T> cls, ModelChange modelChange) {
        changesToImplement.put(cls, modelChange);
    }

    public void executeChanges() {
        this.changesToImplement.forEach((cls, modelChange) -> pistons
                .stream()
                .filter(modelContext -> modelContext.getSchema().getJavaClass().equals(cls))
                .findFirst()
                .orElseThrow(() -> new ElepyConfigException(String.format("No model found with the class: %s", cls.getName())))
                .getModelContext()
                .changeModel(modelChange));
    }

    public void setupDescriptors( HttpService http) {

        final Route build = anElepyRoute()
                .path("/elepy/schemas")
                .addPermissions(Permissions.AUTHENTICATED)
                .method(HttpMethod.GET)

                .route(ctx -> {
                    ctx.type("application/json");

                    ctx.requirePermissions(Permissions.AUTHENTICATED);

                    ctx.response().json(pistons.stream().map(ModelPiston::getSchema).collect(Collectors.toList()));

                })
                .build();

        http.addRoute(build);

    }

    public <T> Schema<T> getModelForClass(Class<T> clazz) {
        return (Schema<T>)
                pistons.stream()
                        .map(ModelPiston::getSchema)
                        .filter(model -> model.getJavaClass().equals(clazz))
                        .findFirst()
                        .orElseThrow(() -> ElepyException.notFound(clazz.getName()));
    }

    public List<Schema<?>> getSchemas() {
        return pistons.stream().map(ModelPiston::getSchema).collect(Collectors.toList());
    }
}
