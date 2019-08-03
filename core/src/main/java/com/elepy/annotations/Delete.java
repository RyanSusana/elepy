package com.elepy.annotations;

import com.elepy.auth.Permissions;
import com.elepy.handlers.DefaultDelete;
import com.elepy.handlers.DeleteHandler;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation used to change the way Elepy handles deletes.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
public @interface Delete {

    /**
     * The class that handles the functionality of deletes on this Resource.
     *
     * @return the route deleteHandler
     * @see DefaultDelete
     * @see com.elepy.handlers.DeleteHandler
     */
    Class<? extends DeleteHandler> handler() default DefaultDelete.class;


    /**
     * A list of required permissions to execute this A
     */
    String[] requiredPermissions() default Permissions.AUTHENTICATED;

}
