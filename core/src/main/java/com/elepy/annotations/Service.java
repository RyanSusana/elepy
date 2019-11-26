package com.elepy.annotations;

import com.elepy.handlers.ServiceHandler;

import java.lang.annotation.*;

/**
 * This defines the initial Service for a model. The service as a whole can be overriden.
 * Just parts of a service can be overriden with ({@link Update}, {@link Delete}, {@link Create}, {@link Find})
 *
 * @see Update
 * @see Delete
 * @see Find
 * @see Create
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
@Inherited
public @interface Service {

    /**
     * @return The class of the service that you are defining.
     */
    Class<? extends ServiceHandler> value();
}
