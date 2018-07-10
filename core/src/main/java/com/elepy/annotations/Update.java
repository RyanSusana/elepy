package com.elepy.annotations;


import com.elepy.models.AccessLevel;
import com.elepy.routes.DefaultUpdate;
import com.elepy.routes.UpdateHandler;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
public @interface Update {
    AccessLevel accessLevel() default AccessLevel.ADMIN;

    Class<? extends UpdateHandler> handler() default DefaultUpdate.class;
}