package com.elepy.mongo.annotations;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
@Repeatable(MongoIndex.List.class)
public @interface MongoIndex {

    String[] properties();

    int text() default -1;

    long expireAfterSeconds() default -1;

    String name() default "";

    boolean unique() default false;

    @Retention(RetentionPolicy.RUNTIME)
    @Target({ElementType.TYPE})
    @interface List {
        MongoIndex[] value();
    }
}
