package com.elepy.annotations;


import com.elepy.models.AccessLevel;
import com.elepy.routes.DefaultFind;
import com.elepy.routes.FindHandler;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
public @interface Find {
    AccessLevel accessLevel() default AccessLevel.ADMIN;
    Class<? extends FindHandler> handler() default DefaultFind.class;
}