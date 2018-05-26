package com.elepy.annotations;


import com.elepy.concepts.IdProvider;
import com.elepy.concepts.ObjectEvaluator;
import com.elepy.dao.CrudProvider;
import com.elepy.dao.MongoProvider;
import com.elepy.dao.SortOption;
import com.elepy.id.HexIdProvider;
import com.elepy.models.RestModelAccessType;
import com.elepy.routes.*;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
public @interface RestModel {

    String name();

    String slug();

    String description() default "";

    String icon() default "file";

    Class<? extends IdProvider> idProvider() default HexIdProvider.class;

    RestModelAccessType findAll() default RestModelAccessType.PUBLIC;

    RestModelAccessType findOne() default RestModelAccessType.PUBLIC;

    RestModelAccessType delete() default RestModelAccessType.ADMIN;

    RestModelAccessType update() default RestModelAccessType.ADMIN;

    RestModelAccessType create() default RestModelAccessType.PUBLIC;

    Class<? extends Create> createRoute() default DefaultCreate.class;

    Class<? extends Find> findRoute() default DefaultFind.class;

    Class<? extends Update> updateRoute() default DefaultUpdate.class;

    Class<? extends FindOne> findOneRoute() default DefaultFindOne.class;

    Class<? extends Delete> deleteRoute() default DefaultDelete.class;


    Class<? extends ObjectEvaluator>[] objectEvaluators() default {};

    Class<? extends CrudProvider> crudProvider() default MongoProvider.class;

    SortOption defaultSortDirection() default SortOption.ASCENDING;

    String defaultSortField() default "_id";

}
