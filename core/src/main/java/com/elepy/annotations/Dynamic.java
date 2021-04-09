package com.elepy.annotations;

import com.elepy.http.HttpMethod;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD, ElementType.TYPE_USE})
public @interface Dynamic {
    HttpMethod method() default HttpMethod.GET;

    String path();

    boolean queryable() default false;

    String mapper() default "";

}
