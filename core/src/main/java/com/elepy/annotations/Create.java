package com.elepy.annotations;


import com.elepy.models.AccessLevel;
import com.elepy.routes.CreateHandler;
import com.elepy.routes.DefaultCreate;
import com.elepy.routes.RouteHandler;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
public @interface Create {
    AccessLevel accessLevel() default AccessLevel.ADMIN;

    Class<? extends RouteHandler> handler() default DefaultCreate.class;
}