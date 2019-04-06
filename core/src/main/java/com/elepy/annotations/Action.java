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

    HttpMethod httpMethod();

    Class<? extends ActionHandler> handler();

    String slug() default "";

    AccessLevel accessLevel() default AccessLevel.PROTECTED;

    ActionType actionType() default ActionType.MULTIPLE;


}
