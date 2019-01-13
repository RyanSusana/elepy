package com.elepy.annotations;

import com.elepy.routes.ServiceHandler;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

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
public @interface Service {

    /**
     * @return The class of the service that you are defining.
     */
    Class<? extends ServiceHandler> value();
}
