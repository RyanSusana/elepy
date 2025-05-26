package com.elepy.igniters;

import com.elepy.annotations.Create;
import com.elepy.annotations.Delete;
import com.elepy.annotations.Find;
import com.elepy.annotations.Update;
import com.elepy.handlers.*;
import com.elepy.schemas.DefaultActions;
import com.elepy.schemas.Schema;
import com.elepy.utils.Annotations;
import jakarta.enterprise.inject.spi.Bean;
import jakarta.enterprise.inject.spi.BeanManager;

import java.util.Optional;
import java.util.function.BiFunction;

public enum DefaultActionFactory {

    CREATE(DefaultActionFactory::createAction),

    FIND_ONE(DefaultActionFactory::findOneAction),
    FIND_MANY(DefaultActionFactory::findManyAction),

    UPDATE_PARTIAL((elepy, schema) -> updateAction(elepy, schema, false)),
    UPDATE_WHOLE((elepy, schema) -> updateAction(elepy, schema, true)),

    DELETE_ONE((elepy, schema) -> deleteAction(elepy, schema, false)),
    DELETE_MANY((elepy, schema) -> deleteAction(elepy, schema, true));

    private final BiFunction<BeanManager, Schema<?>, ModelAction> factoryMethod;

    DefaultActionFactory(BiFunction<BeanManager, Schema<?>, ModelAction> factoryMethod) {
        this.factoryMethod = factoryMethod;
    }

    public BiFunction<BeanManager, Schema<?>, ModelAction> factoryMethod() {
        return factoryMethod;
    }

    private static ModelAction createAction(BeanManager beanManager, Schema<?> schema) {
        Class<? extends ActionHandler> handlerClass = DefaultCreate.class;

        final var definedHandlerOptional = Optional
                .ofNullable(Annotations.get(schema.getJavaClass(), Create.class));


        if (definedHandlerOptional.isPresent()) {
            if (definedHandlerOptional.get().disabled()) {
                return null;
            }
            handlerClass = definedHandlerOptional.get().handler();
        }

        return new ModelAction(DefaultActions.getCreateFromSchema(schema), handlerClass);
    }

    private static ModelAction deleteAction(BeanManager beanManager, Schema<?> schema, boolean many) {
        Class<? extends ActionHandler> handlerClass = DefaultDelete.class;

        final var definedHandlerOptional = Optional
                .ofNullable(Annotations.get(schema.getJavaClass(), Delete.class));

        if (definedHandlerOptional.isPresent()) {
            if (definedHandlerOptional.get().disabled()) {
                return null;
            }
            handlerClass = definedHandlerOptional.get().handler();
        }

        return new ModelAction(DefaultActions.getDeleteFromSchema(schema, many), handlerClass);
    }

    private static ModelAction updateAction(BeanManager beanManager, Schema<?> schema, boolean whole) {
        Class<? extends ActionHandler> handlerClass = DefaultUpdate.class;
        final var definedHandlerOptional = Optional
                .ofNullable(Annotations.get(schema.getJavaClass(), Update.class))
                ;
        if (definedHandlerOptional.isPresent()) {
            if (definedHandlerOptional.get().disabled()) {
                return null;
            }
            handlerClass = definedHandlerOptional.get().handler();
        }

        return new ModelAction(DefaultActions.getUpdateFromSchema(schema, whole), handlerClass);
    }

    private static ModelAction findOneAction(BeanManager beanManager, Schema<?> schema) {
        Class<? extends ActionHandler> handlerClass = DefaultFindOne.class;
        final var definedHandlerOptional = Optional
                .ofNullable(Annotations.get(schema.getJavaClass(), Find.class))
                .map(Find::findOneHandler);
        if (definedHandlerOptional.isPresent()) {
            handlerClass = definedHandlerOptional.get();
        }

        return new ModelAction(DefaultActions.getFindOneFromSchema(schema), handlerClass);
    }

    private static ModelAction findManyAction(BeanManager beanManager, Schema<?> schema) {
        Class<? extends ActionHandler> handlerClass = DefaultFindMany.class;
        final var definedHandlerOptional = Optional
                .ofNullable(Annotations.get(schema.getJavaClass(), Find.class))
                .map(Find::findManyHandler);
        if (definedHandlerOptional.isPresent()) {
            handlerClass = definedHandlerOptional.get();
        }

        return new ModelAction(DefaultActions.getFindManyFromSchema(schema), handlerClass);
    }

    static <T extends ActionHandler<?>> T selectActionHandler(BeanManager beanManager, Class<? extends ActionHandler> actionType) {
        Bean<?> bean = beanManager.resolve(beanManager.getBeans(actionType));
        if (bean != null) {
            return (T) beanManager.getReference(bean, actionType, beanManager.createCreationalContext(bean));
        }
        throw new IllegalArgumentException("No bean found for " + actionType.getName() + " found");
    }

}