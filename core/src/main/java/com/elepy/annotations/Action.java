package com.elepy.annotations;

import com.elepy.http.AccessLevel;
import com.elepy.http.ActionType;
import com.elepy.http.HttpMethod;
import com.elepy.routes.ActionHandler;

import java.lang.annotation.*;

@Repeatable(Actions.class)
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
public @interface Action {

    String name();

    Class<? extends ActionHandler> handler();


    HttpMethod method() default HttpMethod.GET;

    String slug() default "";

    AccessLevel accessLevel() default AccessLevel.PROTECTED;

    ActionType actionType() default ActionType.MULTIPLE;

    /**
     * A list of required permissions to execute this A
     */
    String[] requiredPermissions() default {"protected"};
}
