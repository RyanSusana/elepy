package com.elepy.annotations;


import com.elepy.models.AccessLevel;
import com.elepy.routes.CreateHandler;
import com.elepy.routes.DefaultCreate;
import com.elepy.routes.RouteHandler;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation used to change the way Elepy handles creates.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
public @interface Create {

    /**
     * The protection of the POST request on this resource.
     *
     * @return the access level
     * @see AccessLevel
     */
    AccessLevel accessLevel() default AccessLevel.ADMIN;

    /**
     * The class that handles the functionality of creates on this Resource.
     *
     * @return the route handler
     * @see DefaultCreate
     * @see com.elepy.routes.CreateHandler
     * @see RouteHandler
     */
    Class<? extends CreateHandler> handler() default DefaultCreate.class;
}