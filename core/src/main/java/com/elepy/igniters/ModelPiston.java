package com.elepy.igniters;

import com.elepy.Elepy;
import com.elepy.annotations.CrudFactory;
import com.elepy.annotations.Dao;
import com.elepy.annotations.IdProvider;
import com.elepy.crud.Crud;
import com.elepy.exceptions.Message;
import com.elepy.handlers.ActionHandler;
import com.elepy.handlers.HandlerContext;
import com.elepy.http.*;
import com.elepy.id.DefaultIdentityProvider;
import com.elepy.id.IdentityProvider;
import com.elepy.schemas.Schema;
import com.elepy.utils.Annotations;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.elepy.http.RouteBuilder.anElepyRoute;

public class ModelPiston<T> {

    private final Elepy elepy;
    private final Schema<T> schema;
    private ModelContext<T> modelContext;
    private ModelHandlers<T> serviceExtraction;


    public ModelPiston(Schema<T> schema, Elepy elepy) {
        this.elepy = elepy;
        this.schema = schema;
    }

    void setupDependencies() {
        this.modelContext = extractContext(schema, elepy);
    }

    void setupRouting() {
        this.serviceExtraction = ModelHandlers.createForModel(elepy, schema);
        elepy.addRouting(getAllRoutes());
    }

    public Schema<T> getSchema() {
        return schema;
    }

    public ModelContext<T> getModelContext() {
        return modelContext;
    }

    private List<Route> getAllRoutes() {
        return Stream.of(routesFromDefaultActions(), routesFromCustomActions())
                .flatMap(s -> s)
                .collect(Collectors.toList());
    }

    private Stream<Route> routesFromDefaultActions() {
        return serviceExtraction.
                getDefaultActions().values().stream()
                .map(modelAction -> anElepyRoute()
                        .path(modelAction.getAction().getPath())
                        .permissions(modelAction.getAction().getRequiredPermissions())
                        .method(modelAction.getAction().getMethod())
                        .route(ctx -> modelAction.getActionHandler().handle(new HandlerContext<>(ctx.injectModelClassInHttpContext(schema.getJavaClass()), modelContext)))
                        .build());
    }



    private Stream<Route> routesFromCustomActions() {
        final List<Route> actions = new ArrayList<>();


        for (ModelAction<T> extraAction : serviceExtraction.getExtraActions()) {

            final HttpAction action = extraAction.getAction();
            final ActionHandler<T> actionHandler = extraAction.getActionHandler();

            final RouteBuilder route = anElepyRoute()
                    .permissions(action.getRequiredPermissions())
                    .path(action.getPath() + "/:id")
                    .method(action.getMethod())
                    .route(ctx -> {
                        ctx.attribute("action", action);
                        ctx.result(Message.of("Executed action", 200));

                        actionHandler.handle(new HandlerContext<>(ctx.injectModelClassInHttpContext(schema.getJavaClass()), modelContext));
                    });

            //add two routes for multi select and single select.
            actions.add(route.build());
            actions.add(route.path(action.getPath()).build());
        }
        return actions.stream();
    }



    private static <T> ModelContext<T> extractContext(Schema<T> schema, Elepy elepy) {
        var crud = extractCrud(schema, elepy);
        elepy.registerDependency(Crud.class, schema.getPath(), crud);
        var idProvider = extractIdProvider(schema, elepy);

        return new ModelContext<>(schema, crud, idProvider);
    }

    private static <T> IdentityProvider<T> extractIdProvider(Schema<T> schema, Elepy elepy) {
        var classType = schema.getJavaClass();
        if (classType.isAnnotationPresent(IdProvider.class)) {
            return elepy.initialize(Annotations.get(classType, IdProvider.class).value());
        } else {
            return new DefaultIdentityProvider<>();
        }
    }
    /**
     * Extracts the Crud and returns it
     */
    private static <T> Crud<T> extractCrud(Schema<T> schema, Elepy elepy) {
        var modelType = schema.getJavaClass();
        var annotation = Annotations.get(modelType, CrudFactory.class);

        var crudProvider = annotation == null ?
                elepy.defaultCrudFactory()
                : elepy.initialize(annotation.value());

        final Dao daoAnnotation = Annotations.get(modelType, Dao.class);
        if (daoAnnotation != null) {
            return elepy.initialize(daoAnnotation.value());
        } else {
            return crudProvider.crudFor(schema);
        }
    }
    private HttpContext injectModelClassInHttpContext(HttpContext ctx) {
        return ctx.injectModelClassInHttpContext(schema.getJavaClass());
    }
}
