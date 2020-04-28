package com.elepy.annotations;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
@Repeatable(PredefinedRole.List.class)
@Inherited
public @interface PredefinedRole {

    String id();

    String name();

    String description() default "";

    String[] permissions() default {};

    @Retention(RetentionPolicy.RUNTIME)
    @Target({ElementType.TYPE})
    @Inherited
    @interface List {
        PredefinedRole[] value();
    }
} 
