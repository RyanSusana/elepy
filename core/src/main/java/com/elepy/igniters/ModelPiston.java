package com.elepy.igniters;

import com.elepy.Elepy;
import com.elepy.annotations.Action;
import com.elepy.annotations.ExtraRoutes;
import com.elepy.dao.Crud;
import com.elepy.exceptions.Message;
import com.elepy.handlers.ActionHandler;
import com.elepy.handlers.ServiceHandler;
import com.elepy.http.*;
import com.elepy.models.Schema;
import com.elepy.models.ModelContext;
import com.elepy.utils.ModelUtils;
import com.elepy.utils.ReflectionUtils;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static com.elepy.http.RouteBuilder.anElepyRoute;

public class ModelPiston<T> {

    private final Schema<T> schema;
    private final ModelContext<T> modelContext;
    private final ServiceHandler<T> serviceExtraction;
    private final ObjectMapper objectMapper;


    public ModelPiston(Schema<T> schema, Elepy elepy) {
        this.schema = schema;
        this.objectMapper = elepy.objectMapper();
        this.modelContext = ModelContextExtraction.extractContext(schema, elepy);

        this.serviceExtraction = ModelServiceExtraction.extractService(schema, elepy);
        elepy.addRouting(getAllRoutes(elepy));

        schema.getJavaClass().getAnnotation(ExtraRoutes.class);
    }

    public Schema<T> getSchema() {
        return schema;
    }

    public ModelContext<T> getModelContext() {
        return modelContext;
    }

    public ServiceHandler<T> getServiceExtraction() {
        return serviceExtraction;
    }

    private List<Route> getAllRoutes(Elepy elepy) {
        Crud<T> dao = modelContext.getCrud();

        List<Route> toReturn = new ArrayList<>();
        //POST
        toReturn.add(anElepyRoute()
                .path(schema.getPath())
                .addPermissions(schema.getCreateAction().getRequiredPermissions())
                .method(HttpMethod.POST)
                .route(ctx -> serviceExtraction.handleCreate(injectModelClassInHttpContext(ctx), dao, modelContext, objectMapper))
                .build()
        );

        // PUT
        toReturn.add(anElepyRoute()
                .path(schema.getPath() + "/:id")
                .addPermissions(schema.getUpdateAction().getRequiredPermissions())
                .method(HttpMethod.PUT)
                .route(ctx -> serviceExtraction.handleUpdatePut(injectModelClassInHttpContext(ctx), dao, modelContext, objectMapper))
                .build()
        );

        //PATCH
        toReturn.add(anElepyRoute()
                .path(schema.getPath() + "/:id")
                .method(HttpMethod.PATCH)
                .addPermissions(schema.getUpdateAction().getRequiredPermissions())
                .route(ctx -> {
                    ctx.request().attribute("modelClass", schema.getJavaClass());
                    serviceExtraction.handleUpdatePatch(injectModelClassInHttpContext(ctx), dao, modelContext, objectMapper);
                })
                .build()
        );

        // DELETE
        toReturn.add(anElepyRoute()
                .path(schema.getPath() + "/:id")
                .method(HttpMethod.DELETE)
                .addPermissions(schema.getDeleteAction().getRequiredPermissions())
                .route(ctx -> serviceExtraction.handleDelete(injectModelClassInHttpContext(ctx), dao, modelContext, objectMapper))
                .build()
        );
        toReturn.add(anElepyRoute()
                .path(schema.getPath())
                .method(HttpMethod.DELETE)
                .addPermissions(schema.getDeleteAction().getRequiredPermissions())
                .route(ctx -> serviceExtraction.handleDelete(injectModelClassInHttpContext(ctx), dao, modelContext, objectMapper))
                .build()
        );

        //GET PAGE
        toReturn.add(anElepyRoute()
                .path(schema.getPath())
                .method(HttpMethod.GET)
                .addPermissions(schema.getFindManyAction().getRequiredPermissions())
                .route(ctx -> serviceExtraction.handleFindMany(injectModelClassInHttpContext(ctx), dao, modelContext, objectMapper))
                .build()
        );

        //GET ONE
        toReturn.add(anElepyRoute()
                .path(schema.getPath() + "/:id")
                .method(HttpMethod.GET)
                .addPermissions(schema.getFindOneAction().getRequiredPermissions())
                .route(ctx -> serviceExtraction.handleFindOne(injectModelClassInHttpContext(ctx), dao, modelContext, objectMapper))
                .build()
        );

        //Extra Routes
        toReturn.addAll(getActionRoutes(elepy));
        toReturn.addAll(getExtraRoutes(elepy));
        return toReturn;
    }

    private List<Route> getExtraRoutes(Elepy elepy) {
        final ExtraRoutes extraRoutesAnnotation = schema.getJavaClass().getAnnotation(ExtraRoutes.class);

        if (extraRoutesAnnotation == null) {
            return new ArrayList<>();
        } else {
            return Arrays.stream(extraRoutesAnnotation.value())
                    .map(aClass -> ReflectionUtils.scanForRoutes(elepy.initialize(aClass)))
                    .flatMap(List::stream)
                    .collect(Collectors.toList());
        }

    }

    private List<Route> getActionRoutes(Elepy elepy) {

        var modelType = modelContext.getSchema().getJavaClass();
        var path = modelContext.getSchema().getPath();
        var crud = modelContext.getCrud();
        final Action[] actionAnnotations = modelType.getAnnotationsByType(Action.class);
        final List<Route> actions = new ArrayList<>();

        for (Action actionAnnotation : actionAnnotations) {

            final HttpAction action = ModelUtils.actionToHttpAction(path , actionAnnotation);
            final ActionHandler<T> actionHandler = elepy.initialize(actionAnnotation.handler());

            final RouteBuilder route = anElepyRoute()
                    .addPermissions(actionAnnotation.requiredPermissions())
                    .path(action.getPath() + "/:id")
                    .method(actionAnnotation.method())
                    .route(ctx -> {
                        ctx.attribute("action", action);
                        ctx.result(Message.of("Executed action", 200));
                        actionHandler.handleAction(ctx.injectModelClassInHttpContext(modelType), crud, modelContext, elepy.objectMapper());
                    });

            //add two routes for multi select and single select.
            actions.add(route.build());
            actions.add(route.path(action.getPath()).build());
        }
        return actions;
    }


    private HttpContext injectModelClassInHttpContext(HttpContext ctx) {
        return ctx.injectModelClassInHttpContext(schema.getJavaClass());
    }
}
