package com.elepy.annotations;


import com.elepy.models.AccessLevel;
import com.elepy.routes.DefaultFind;
import com.elepy.routes.RouteHandler;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation used to change the way Elepy handles finds.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
public @interface Find {

    /**
     * The protection of the GET request on this resource.
     *
     * @return the access level
     * @see AccessLevel
     */
    AccessLevel accessLevel() default AccessLevel.PUBLIC;

    /**
     * The class that handles the functionality of finds on this Resource.
     *
     * @return the route handler
     * @see DefaultFind
     * @see com.elepy.routes.FindHandler
     * @see RouteHandler
     */
    Class<? extends RouteHandler> handler() default DefaultFind.class;
}