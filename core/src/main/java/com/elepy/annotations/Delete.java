package com.elepy.annotations;

import com.elepy.http.AccessLevel;
import com.elepy.routes.DefaultDelete;
import com.elepy.routes.DeleteHandler;

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
     * The protection of the DELETE request on this resource.
     *
     * @return the access level
     * @see AccessLevel
     */
    AccessLevel accessLevel() default AccessLevel.PROTECTED;

    /**
     * The class that handles the functionality of deletes on this Resource.
     *
     * @return the route deleteHandler
     * @see DefaultDelete
     * @see com.elepy.routes.DeleteHandler
     */
    Class<? extends DeleteHandler> handler() default DefaultDelete.class;

}
