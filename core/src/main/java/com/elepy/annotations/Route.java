package com.elepy.annotations;

import com.elepy.models.AccessLevel;
import spark.route.HttpMethod;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Signifies a route in a class.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD})
public @interface Route {
    /**
     * @return The URI path of the route
     */
    String path();

    /**
     * @return The HTTP request method of the route
     */
    HttpMethod requestMethod();

    /**
     * @return The accessLevel of the method.
     */
    AccessLevel accessLevel() default AccessLevel.PUBLIC;
}
