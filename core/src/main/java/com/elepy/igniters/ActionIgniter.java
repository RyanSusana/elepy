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


    private final ModelDescription<T> modelDescription;
    private final Crud<T> crud;
    private final Elepy elepy;

    private final Slugify slugify = new Slugify();


    public ActionIgniter(ModelDescription<T> modelDescription, Crud<T> crud, Elepy elepy) {
        this.modelDescription = modelDescription;
        this.crud = crud;
        this.elepy = elepy;
    }

    public List<HttpAction> ignite() {

        final Action[] actionAnnotations = modelDescription.getModelType().getAnnotationsByType(Action.class);
        final List<HttpAction> actions = new ArrayList<>();

        for (Action actionAnnotation : actionAnnotations) {
            try {
                final String baseSlug = modelDescription.getSlug() + "/actions" + (actionAnnotation.slug().isEmpty() ? "/" + slugify.slugify(actionAnnotation.name()) : actionAnnotation.slug());
                final String slug = baseSlug + "/:id";

                HttpAction action = HttpAction.of(actionAnnotation.name(), baseSlug, actionAnnotation.accessLevel(), actionAnnotation.method(), actionAnnotation.actionType());

                actions.add(action);
                final ActionHandler<T> actionHandler = elepy.initializeElepyObject(actionAnnotation.handler());
                final RouteBuilder route = anElepyRoute()
                        .accessLevel(actionAnnotation.accessLevel())
                        .path(slug)
                        .method(actionAnnotation.method())
                        .route(ctx -> {
                            ctx.attribute("action", action);
                            ctx.result(Message.of("Executed action", 200));
                            actionHandler.handleAction(ctx.injectModelClassInHttpContext(modelDescription.getModelType()), crud, modelDescription, elepy.getObjectMapper());
                        });

                //add two routes for multi select and single select.
                elepy.addRouting(route.build());
                elepy.addRouting(route.path(baseSlug).build());
            } catch (IllegalAccessException | InvocationTargetException | InstantiationException e) {
                throw new ElepyConfigException(String.format("Error igniting action '%s' on %s", actionAnnotation.name(), modelDescription.getModelType().getName()));
            }
        }
        return actions;
    }
}
