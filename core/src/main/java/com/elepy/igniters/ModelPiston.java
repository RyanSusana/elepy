package com.elepy.igniters;

import com.elepy.Elepy;
import com.elepy.annotations.ExtraRoutes;
import com.elepy.exceptions.Message;
import com.elepy.handlers.ActionHandler;
import com.elepy.handlers.HandlerContext;
import com.elepy.http.HttpAction;
import com.elepy.http.HttpContext;
import com.elepy.http.Route;
import com.elepy.http.RouteBuilder;
import com.elepy.models.ModelContext;
import com.elepy.models.Schema;
import com.elepy.utils.Annotations;
import com.elepy.utils.ReflectionUtils;

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
        this.modelContext = ModelContextExtraction.extractContext(schema, elepy);
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
        return Stream.of(routesFromDefaultActions(), routesFromCustomActions(), routesFromAnnotation())
                .flatMap(s -> s)
                .collect(Collectors.toList());
    }

    private Stream<Route> routesFromDefaultActions() {
        return serviceExtraction.
                getDefaultActions().values().stream()
                .map(modelAction -> anElepyRoute()
                        .path(modelAction.getAction().getPath())
                        .addPermissions(modelAction.getAction().getRequiredPermissions())
                        .method(modelAction.getAction().getMethod())
                        .route(ctx -> modelAction.getActionHandler().handle(new HandlerContext<>(ctx.injectModelClassInHttpContext(schema.getJavaClass()), modelContext)))
                        .build());
    }

    private Stream<Route> routesFromAnnotation() {
        final ExtraRoutes extraRoutesAnnotation = Annotations.get(schema.getJavaClass(), ExtraRoutes.class);

        if (extraRoutesAnnotation == null) {
            return Stream.empty();
        } else {
            return Arrays.stream(extraRoutesAnnotation.value())
                    .map(aClass -> ReflectionUtils.scanForRoutes(elepy.initialize(aClass)))
                    .flatMap(List::stream);
        }

    }

    private Stream<Route> routesFromCustomActions() {
        final List<Route> actions = new ArrayList<>();


        for (ModelAction<T> extraAction : serviceExtraction.getExtraActions()) {

            final HttpAction action = extraAction.getAction();
            final ActionHandler<T> actionHandler = extraAction.getActionHandler();

            final RouteBuilder route = anElepyRoute()
                    .addPermissions(action.getRequiredPermissions())
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


    private HttpContext injectModelClassInHttpContext(HttpContext ctx) {
        return ctx.injectModelClassInHttpContext(schema.getJavaClass());
    }
}
