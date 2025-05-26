package com.elepy.annotations;


import com.elepy.handlers.ActionHandler;
import com.elepy.handlers.DefaultUpdate;

import java.lang.annotation.*;

/**
 * Annotation used to change the way Elepy handles updates.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})

@Inherited
public @interface Update {

    /**
     * The class that handles the functionality of deletes on this Resource.
     *
     * @return the route updateHandler
     * @see com.elepy.handlers.SimpleUpdate
     * @see DefaultUpdate
     */
    Class<? extends ActionHandler> handler() default DefaultUpdate.class;

    /**
     * A list of required permissions to execute this A
     */
    String[] requiredPermissions() default {"resources.update"};

    boolean disabled() default false;
}