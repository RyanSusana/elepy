package com.elepy.annotations;

import com.elepy.auth.Permissions;
import com.elepy.handlers.ActionHandler;
import com.elepy.http.ActionType;
import com.elepy.http.HttpMethod;

import java.lang.annotation.*;

@Repeatable(Actions.class)
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
public @interface Action {

    String name();

    Class<? extends ActionHandler> handler();


    HttpMethod method() default HttpMethod.GET;

    String slug() default "";

    ActionType actionType() default ActionType.MULTIPLE;

    /**
     * A list of required permissions to execute this A
     */
    String[] requiredPermissions() default Permissions.AUTHENTICATED;
}
