package com.elepy.annotations;

import com.elepy.handlers.ActionHandler;
import com.elepy.http.HttpMethod;

import java.lang.annotation.*;

@Repeatable(Action.List.class)
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
@Inherited
public @interface Action {

    String name();

    Class<? extends ActionHandler> handler();

    HttpMethod method() default HttpMethod.POST;

    String path() default "";

    Class<?> input() default Object.class;

    boolean singleRecord() default true;

    boolean multipleRecords() default true;

    String description() default "";

    String warning() default "";

    /**
     * A list of required permissions to execute this Action
     */
    String[] requiredPermissions() default {};


    @Retention(RetentionPolicy.RUNTIME)
    @Target({ElementType.TYPE})
    @Inherited
    @interface List {
        Action[] value();
    }
}
