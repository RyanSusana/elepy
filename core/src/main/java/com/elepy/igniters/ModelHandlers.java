package com.elepy.igniters;

import com.elepy.Elepy;
import com.elepy.annotations.*;
import com.elepy.handlers.*;
import com.elepy.schemas.ActionFactory;
import com.elepy.schemas.Schema;
import com.elepy.utils.Annotations;
import com.elepy.schemas.DefaultActions;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.stream.Collectors;


public class ModelHandlers<T> {


    private final ActionFactory actionFactory;
    private final Map<Default, ModelAction<T>> defaultActions;
    private final List<ModelAction<T>> extraActions;

    @SuppressWarnings("unchecked")
    private ModelHandlers(Elepy elepy, Schema<T> schema) {
        actionFactory = new ActionFactory();
        this.defaultActions = Arrays.stream(Default.values())
                .collect(Collectors.toMap(value -> value,
                        value -> value.schemaToHttpAction.apply(elepy, schema)
                ));

        this.extraActions = Arrays.stream(schema.getJavaClass().getAnnotationsByType(Action.class))
                .map(action -> new ModelAction<>(
                        actionFactory.actionToHttpAction(schema.getPath(), action),
                        initializeActionHandler(elepy, action))
                ).collect(Collectors.toList());
    }

    private ActionHandler<T> initializeActionHandler(Elepy elepy, Action action) {
        return (ActionHandler<T>) elepy.initialize(action.handler());
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
                    .ofNullable(Annotations.get(schema.getJavaClass(), Delete.class));
            final ActionHandler<?> handler = annotation
                    .map(anno -> (ActionHandler<?>) elepy
                            .initialize(anno.handler()))
                    .orElse(new DefaultDelete());


            return new ModelAction<>(DefaultActions.getDeleteFromSchema(schema, many), handler);
        }

        private static ModelAction updateAction(Elepy elepy, Schema<?> schema, boolean whole) {
            final var annotation = Optional
                    .ofNullable(Annotations.get(schema.getJavaClass(), Update.class));


            final ActionHandler<?> handler = annotation.map(anno -> (ActionHandler<?>) elepy.initialize(anno.handler())).orElse(new DefaultUpdate<>());

            return new ModelAction<>(DefaultActions.getUpdateFromSchema(schema, whole), handler);
        }

        private static ModelAction findOneAction(Elepy elepy, Schema<?> schema) {
            final var annotation = Optional
                    .ofNullable(Annotations.get(schema.getJavaClass(), Find.class));


            final ActionHandler<?> handler = annotation.map(anno -> (ActionHandler<?>) elepy.initialize(anno.findOneHandler())).orElse(new DefaultFindOne<>());

            return new ModelAction<>(DefaultActions.getFindOneFromSchema(schema), handler);
        }

        private static ModelAction findManyAction(Elepy elepy, Schema<?> schema) {
            final var annotation = Optional
                    .ofNullable(Annotations.get(schema.getJavaClass(), Find.class));

            final ActionHandler<?> handler = annotation.map(anno -> (ActionHandler<?>) elepy.initialize(anno.findManyHandler())).orElse(new DefaultFindMany<>());

            return new ModelAction<>(DefaultActions.getFindFromSchema(schema), handler);
        }

        private static ModelAction createAction(Elepy elepy, Schema<?> schema) {

            final var annotation = Optional
                    .ofNullable(Annotations.get(schema.getJavaClass(), Create.class));


            final ActionHandler<?> handler = annotation.map(anno -> (ActionHandler<?>) elepy.initialize(anno.handler())).orElse(new DefaultCreate<>());

            return new ModelAction<>(DefaultActions.getCreateFromSchema(schema),
                    handler);
        }


    }
} 
