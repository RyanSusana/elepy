package com.elepy.annotations;

import com.elepy.handlers.ActionHandler;
import com.elepy.handlers.DefaultDelete;

import java.lang.annotation.*;

/**
 * Annotation used to change the way Elepy handles deletes.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
@Inherited
public @interface Delete {

    /**
     * The class that handles the functionality of deletes on this Resource.
     *
     * @return the route deleteHandler
     * @see DefaultDelete
     */
    Class<? extends ActionHandler> handler() default DefaultDelete.class;


    /**
     * A list of required permissions to execute this A
     */
    String[] requiredPermissions() default {"resources.delete"} ;

    boolean disabled() default false;

}
