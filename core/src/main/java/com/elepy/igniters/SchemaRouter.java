package com.elepy.igniters;

import com.elepy.annotations.*;
import com.elepy.handlers.HandlerContext;
import com.elepy.http.HttpService;
import com.elepy.http.HttpServiceInterceptor;
import com.elepy.http.RouteBuilder;
import com.elepy.schemas.ActionFactory;
import com.elepy.schemas.Schema;
import com.elepy.schemas.SchemaRegistry;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.spi.BeanManager;
import jakarta.enterprise.inject.spi.CDI;
import jakarta.inject.Inject;

import java.util.ArrayList;
import java.util.List;

@ApplicationScoped
public class SchemaRouter {

    @Inject
    private SchemaRegistry schemaRegistry;

    @Inject
    private BeanManager beanManager;

    @Inject
    private ActionFactory actionFactory;

    @Inject
    private HttpServiceInterceptor httpService;

    public void setupRoutingForSchemas() {

        var allActions = new ArrayList<ModelAction>();

        for (var schema : schemaRegistry.getSchemas()) {
            List<ModelAction> actions = getActionsForClass(schema);

            for (var action : actions) {
                var actionHandlerClass = action.getActionHandler();

                var actionDescription = action.getAction();

                httpService.addRoute(RouteBuilder.anElepyRoute()
                        .path(actionDescription.getPath())
                        .acceptType("application/json")
                        .method(actionDescription.getMethod())
                        .permissions(actionDescription.getRequiredPermissions())
                        .route(ctx -> {

                            // Inject the schema class as an attribute so that it can be used in the action handler.
                            ctx.request().attribute("schemaClass", schema.getJavaClass());

                            var modelDetailsFactory = CDI.current().select(ModelDetailsFactory.class).get();
                            var actionHandler = DefaultActionFactory.selectActionHandler(beanManager, actionHandlerClass);

                            var modelCtx = modelDetailsFactory.getModelDetailsFor(schema.getJavaClass());
                            var handlerContext = new HandlerContext<>(ctx, modelCtx);
                            actionHandler.handle(handlerContext);
                        })
                        .build());
            }
        }
    }

    public <T> List<ModelAction> getActionsForClass(Schema<T> schema) {
        List<ModelAction> actions = new ArrayList<>();

        final var defaultActions = getDefaultActions(schema);
        final var extraActions = getExtraActions(schema);

        actions.addAll(defaultActions);
        actions.addAll(extraActions);

        return actions;
    }

    private <T> List<ModelAction> getDefaultActions(Schema<T> schema) {
        var actions = new ArrayList<ModelAction>();
        var allDefaultActions = DefaultActionFactory.values();
        for (var allDefaultAction : allDefaultActions) {
            var factory = allDefaultAction.factoryMethod();
            var action = factory.apply(beanManager, schema);

            // Can be disabled :)
            if (action!= null) actions.add(action);
        }
        return actions;
    }

    private <T> List<ModelAction> getExtraActions(Schema<T> schema) {
        List<ModelAction> actions = new ArrayList<>();

        for (var action : schema.getJavaClass().getAnnotationsByType(Action.class)) {

            var handlerClass = action.handler();

            var modelAction = new ModelAction(
                    actionFactory.actionToHttpAction(schema.getPath(), action), handlerClass);
            actions.add(modelAction);
        }

        return actions;
    }

}
