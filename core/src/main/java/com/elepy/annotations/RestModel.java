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





    SortOption defaultSortDirection() default SortOption.ASCENDING;

    String defaultSortField() default "_id";

}
