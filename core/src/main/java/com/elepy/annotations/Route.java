package com.elepy.annotations;

import com.elepy.auth.Permissions;
import com.elepy.http.HttpMethod;

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
    HttpMethod method();


    /**
     * A list of required permissions to execute this A
     */
    String[] requiredPermissions() default Permissions.AUTHENTICATED;
}
