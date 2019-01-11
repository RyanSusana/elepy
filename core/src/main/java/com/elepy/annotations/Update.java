package com.elepy.annotations;


import com.elepy.models.AccessLevel;
import com.elepy.routes.DefaultUpdate;
import com.elepy.routes.RouteHandler;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation used to change the way Elepy handles updates.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
public @interface Update {

    /**
     * The protection of the PUT and PATCH requests on this resource.
     *
     * @return the access level
     * @see AccessLevel
     */
    AccessLevel accessLevel() default AccessLevel.ADMIN;

    /**
     * The class that handles the functionality of deletes on this Resource.
     *
     * @return the route handler
     * @see com.elepy.routes.SimpleUpdate
     * @see DefaultUpdate
     * @see com.elepy.routes.UpdateHandler
     * @see RouteHandler
     */
    Class<? extends RouteHandler> handler() default DefaultUpdate.class;
}