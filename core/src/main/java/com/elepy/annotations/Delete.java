package com.elepy.annotations;

import com.elepy.models.RestModelAccessType;
import com.elepy.routes.DefaultDelete;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
public @interface Delete {
    RestModelAccessType accessLevel() default RestModelAccessType.ADMIN;
    Class<? extends com.elepy.routes.Delete> implementation() default DefaultDelete.class;
}
