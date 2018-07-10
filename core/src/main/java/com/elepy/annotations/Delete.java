package com.elepy.annotations;

import com.elepy.models.AccessLevel;
import com.elepy.routes.DefaultDelete;
import com.elepy.routes.DeleteHandler;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
public @interface Delete {
    AccessLevel accessLevel() default AccessLevel.ADMIN;

    Class<? extends DeleteHandler> handler() default DefaultDelete.class;
}
