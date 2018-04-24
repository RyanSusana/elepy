package com.ryansusana.elepy.annotations;


import com.ryansusana.elepy.concepts.ObjectEvaluator;
import com.ryansusana.elepy.models.IdGenerationType;
import com.ryansusana.elepy.models.RestModelAccessType;
import com.ryansusana.elepy.routes.*;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface RestModel {

    String name();

    String slug();

    String description() default "";

    String icon() default "file";

    IdGenerationType idGenerator() default IdGenerationType.NONE;

    RestModelAccessType findAll() default RestModelAccessType.PUBLIC;

    RestModelAccessType findOne() default RestModelAccessType.PUBLIC;

    RestModelAccessType delete() default RestModelAccessType.ADMIN;

    RestModelAccessType update() default RestModelAccessType.ADMIN;

    RestModelAccessType create() default RestModelAccessType.PUBLIC;

    Class<? extends Create> createRoute() default CreateImpl.class;

    Class<? extends Find> findRoute() default FindImpl.class;

    Class<? extends Update> updateRoute() default UpdateImpl.class;

    Class<? extends FindOne> findOneRoute() default FindOneImpl.class;

    Class<? extends Delete> deleteRoute() default DeleteImpl.class;


    Class<? extends ObjectEvaluator>[] objectEvaluators() default {};


}
