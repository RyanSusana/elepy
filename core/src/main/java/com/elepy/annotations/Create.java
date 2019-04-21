package com.elepy.annotations;


import com.elepy.http.AccessLevel;
import com.elepy.routes.CreateHandler;
import com.elepy.routes.DefaultCreate;

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
    AccessLevel accessLevel() default AccessLevel.PROTECTED;

    /**
     * The class that handles the functionality of creates on this Resource.
     *
     * @return the route createHandler
     * @see DefaultCreate
     * @see com.elepy.routes.CreateHandler
     */
    Class<? extends CreateHandler> handler() default DefaultCreate.class;

    /**
     * A list of required permissions to execute this A
     */
    String[] requiredPermissions() default {"protected"};
}