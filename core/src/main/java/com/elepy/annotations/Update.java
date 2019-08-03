package com.elepy.annotations;


import com.elepy.auth.Permissions;
import com.elepy.handlers.DefaultUpdate;
import com.elepy.handlers.UpdateHandler;

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
     * The class that handles the functionality of deletes on this Resource.
     *
     * @return the route updateHandler
     * @see com.elepy.handlers.SimpleUpdate
     * @see DefaultUpdate
     * @see com.elepy.handlers.UpdateHandler
     */
    Class<? extends UpdateHandler> handler() default DefaultUpdate.class;

    /**
     * A list of required permissions to execute this A
     */
    String[] requiredPermissions() default Permissions.AUTHENTICATED;
}