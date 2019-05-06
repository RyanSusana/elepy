package com.elepy.igniters;

import com.elepy.Elepy;
import com.elepy.annotations.Action;
import com.elepy.dao.Crud;
import com.elepy.describers.ModelDescription;
import com.elepy.exceptions.ElepyConfigException;
import com.elepy.exceptions.Message;
import com.elepy.http.HttpAction;
import com.elepy.http.RouteBuilder;
import com.elepy.routes.ActionHandler;
import com.github.slugify.Slugify;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

import static com.elepy.http.RouteBuilder.anElepyRoute;

public class ActionIgniter<T> {


    private static final Slugify slugify = new Slugify();
    private final Class<T> modelType;
    private final Crud<T> crud;
    private final Elepy elepy;
    private final String slug;


    public ActionIgniter(Class<T> modelType, String slug, Crud<T> crud, Elepy elepy) {
        this.modelType = modelType;
        this.slug = slug;
        this.crud = crud;
        this.elepy = elepy;
    }

    public static HttpAction actionToHttpAction(String baseSlug, Action actionAnnotation) {
        final String multiSlug = baseSlug + "/actions" + (actionAnnotation.slug().isEmpty() ? "/" + slugify.slugify(actionAnnotation.name()) : actionAnnotation.slug());

        return HttpAction.of(actionAnnotation.name(), multiSlug, actionAnnotation.requiredPermissions(), actionAnnotation.method(), actionAnnotation.actionType());

    }

    public List<HttpAction> ignite() {

        final Action[] actionAnnotations = modelType.getAnnotationsByType(Action.class);
        final List<HttpAction> actions = new ArrayList<>();

        for (Action actionAnnotation : actionAnnotations) {
            try {

                final HttpAction action = actionToHttpAction(this.slug, actionAnnotation);
                actions.add(action);
                final ActionHandler<T> actionHandler = elepy.initializeElepyObject(actionAnnotation.handler());

                final RouteBuilder route = anElepyRoute()
                        .addPermissions(actionAnnotation.requiredPermissions())
                        .path(action.getSlug() + "/:id")
                        .method(actionAnnotation.method())
                        .route(ctx -> {
                            ctx.attribute("action", action);
                            ctx.result(Message.of("Executed action", 200));
                            final ModelDescription<T> modelDescription = elepy.getModelDescriptionFor(modelType);
                            actionHandler.handleAction(ctx.injectModelClassInHttpContext(modelType), crud, modelDescription, elepy.getObjectMapper());
                        });

                //add two routes for multi select and single select.
                elepy.addRouting(route.build());
                elepy.addRouting(route.path(action.getSlug()).build());
            } catch (IllegalAccessException | InvocationTargetException | InstantiationException e) {
                throw new ElepyConfigException(String.format("Error igniting action '%s' on %s", actionAnnotation.name(), modelType.getName()));
            }
        }
        return actions;
    }
}
