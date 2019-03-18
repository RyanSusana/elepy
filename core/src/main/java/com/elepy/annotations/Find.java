package com.elepy.annotations;


import com.elepy.http.AccessLevel;
import com.elepy.routes.DefaultFindMany;
import com.elepy.routes.DefaultFindOne;
import com.elepy.routes.FindManyHandler;
import com.elepy.routes.FindOneHandler;

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
     * @return the route findManyHandler
     * @see DefaultFindMany
     * @see FindManyHandler
     */
    Class<? extends FindManyHandler> findManyHandler() default DefaultFindMany.class;

    /**
     * The class that handles the functionality of finds on this Resource.
     *
     * @return the route findOneHandler
     * @see DefaultFindMany
     * @see FindManyHandler
     */
    Class<? extends FindOneHandler> findOneHandler() default DefaultFindOne.class;
}