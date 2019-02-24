package com.elepy.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Marks classes for having extra {@link Route} methods.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
public @interface ExtraRoutes {
    /**
     * @return The classes that have @HttpContextHandler annotations
     */
    Class<?>[] value();
}
