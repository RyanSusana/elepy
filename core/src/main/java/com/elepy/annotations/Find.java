package com.elepy.annotations;


import com.elepy.handlers.DefaultFindMany;
import com.elepy.handlers.DefaultFindOne;
import com.elepy.handlers.FindManyHandler;
import com.elepy.handlers.FindOneHandler;

import java.lang.annotation.*;

/**
 * Annotation used to change the way Elepy handles finds.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
@Inherited
public @interface Find {


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

    /**
     * A list of required permissions to execute this A
     */
    String[] requiredPermissions() default {};
}