package com.elepy.igniters;

import com.elepy.Elepy;
import com.elepy.annotations.*;
import com.elepy.auth.Permissions;
import com.elepy.handlers.*;
import com.elepy.http.HttpAction;
import com.elepy.http.HttpMethod;
import com.elepy.models.Schema;
import com.elepy.utils.ModelUtils;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.stream.Collectors;


public class ModelHandlers<T> {


    private final Map<Default, ModelAction<T>> defaultActions;
    private final List<ModelAction<T>> extraActions;

    @SuppressWarnings("unchecked")
    private ModelHandlers(Elepy elepy, Schema<T> schema) {

        this.defaultActions = Arrays.stream(Default.values())
                .collect(Collectors.toMap(value -> value,
                        value -> value.schemaToHttpAction.apply(elepy, schema)
                ));

        this.extraActions = Arrays.stream(schema.getJavaClass().getAnnotationsByType(Action.class))
                .map(action -> new ModelAction<>(
                        ModelUtils.actionToHttpAction(schema.getPath(), action),
                        (ActionHandler<T>) elepy.initialize(action.handler()))).collect(Collectors.toList()
                );
    }

    public static <T> ModelHandlers<T> createForModel(Elepy elepy, Schema<T> schema) {
        return new ModelHandlers<>(elepy, schema);
    }

    public Map<Default, ModelAction<T>> getDefaultActions() {
        return defaultActions;
    }

    public List<ModelAction<T>> getExtraActions() {
        return extraActions;
    }

    public enum Default {

        CREATE(Default::createAction),

        FIND_ONE(Default::findOneAction),
        FIND_MANY(Default::findManyAction),

        UPDATE_PARTIAL((elepy, schema) -> updateAction(elepy, schema, false)),
        UPDATE_WHOLE((elepy, schema) -> updateAction(elepy, schema, true)),

        DELETE_ONE((elepy, schema) -> deleteAction(elepy, schema, false)),
        DELETE_MANY((elepy, schema) -> deleteAction(elepy, schema, true));


        private final BiFunction<Elepy, Schema<?>, ModelAction> schemaToHttpAction;


        Default(BiFunction<Elepy, Schema<?>, ModelAction> schemaToHttpAction) {
            this.schemaToHttpAction = schemaToHttpAction;
        }

        private static ModelAction deleteAction(Elepy elepy, Schema<?> schema, boolean many) {
            final var annotation = Optional
                    .ofNullable(schema.getJavaClass().getAnnotation(Delete.class));

            var path = schema.getPath();
            if (!many) path += "/:id";

            final var permissions = annotation
                    .map(Delete::requiredPermissions)
                    .orElse(Permissions.DEFAULT);

            final ActionHandler<?> handler = annotation
                    .map(anno -> (ActionHandler<?>) elepy
                            .initialize(anno.handler()))
                    .orElse(new DefaultDelete());


            return new ModelAction<>(new HttpAction(
                    "Delete",
                    path,
                    permissions,
                    HttpMethod.DELETE,
                    true,
                    true,
                    "Deletes the selected records",
                    "Are you sure that you want to delete these record(s)",
                    null), handler);
        }

        private static ModelAction updateAction(Elepy elepy, Schema<?> schema, boolean whole) {
            final var annotation = Optional
                    .ofNullable(schema.getJavaClass().getAnnotation(Update.class));

            final var permissions = annotation
                    .map(Update::requiredPermissions)
                    .orElse(Permissions.DEFAULT);

            final ActionHandler<?> handler = annotation.map(anno -> (ActionHandler<?>) elepy.initialize(anno.handler())).orElse(new DefaultUpdate<>());

            return new ModelAction<>(new HttpAction(
                    "Update",
                    schema.getPath() + "/:id",
                    permissions,
                    whole ? HttpMethod.PUT : HttpMethod.PATCH,
                    true,
                    false,
                    "",
                    "",
                    null), handler);
        }

        private static ModelAction findOneAction(Elepy elepy, Schema<?> schema) {
            final var annotation = Optional
                    .ofNullable(schema.getJavaClass().getAnnotation(Find.class));


            final ActionHandler<?> handler = annotation.map(anno -> (ActionHandler<?>) elepy.initialize(anno.findOneHandler())).orElse(new DefaultFindOne<>());

            return new ModelAction<>(new HttpAction(
                    "Find One",
                    schema.getPath() + "/:id",
                    getFindPermissions(schema),
                    HttpMethod.GET,
                    false,
                    false,
                    "",
                    "",
                    null), handler);
        }

        private static ModelAction findManyAction(Elepy elepy, Schema<?> schema) {
            final var annotation = Optional
                    .ofNullable(schema.getJavaClass().getAnnotation(Find.class));

            final ActionHandler<?> handler = annotation.map(anno -> (ActionHandler<?>) elepy.initialize(anno.findManyHandler())).orElse(new DefaultFindMany<>());

            return new ModelAction<>(new HttpAction("Find Many", schema.getPath(),
                    getFindPermissions(schema), HttpMethod.GET, true, true, "", "", null), handler);
        }

        private static ModelAction createAction(Elepy elepy, Schema<?> schema) {

            final var annotation = Optional
                    .ofNullable(schema.getJavaClass().getAnnotation(Create.class));
            final var permissions = annotation
                    .map(Create::requiredPermissions)
                    .orElse(Permissions.DEFAULT);


            final ActionHandler<?> handler = annotation.map(anno -> (ActionHandler<?>) elepy.initialize(anno.handler())).orElse(new DefaultCreate<>());

            return new ModelAction<>(new HttpAction("Create", schema.getPath(),
                    permissions, HttpMethod.POST, true, true, "", "", null),

                    handler);
        }

        private static String[] getFindPermissions(Schema<?> schema) {
            return Optional
                    .ofNullable(schema.getJavaClass().getAnnotation(Find.class))
                    .map(Find::requiredPermissions)
                    .orElse(Permissions.NONE);
        }


    }
} 
