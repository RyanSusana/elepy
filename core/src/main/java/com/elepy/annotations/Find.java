package com.elepy.annotations;


import com.elepy.models.RestModelAccessType;
import com.elepy.routes.DefaultDelete;
import com.elepy.routes.DefaultFind;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
public @interface Find {
    RestModelAccessType accessLevel() default RestModelAccessType.ADMIN;
    Class<? extends com.elepy.routes.Find> implementation() default DefaultFind.class;
}