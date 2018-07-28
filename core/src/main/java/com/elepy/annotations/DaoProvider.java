package com.elepy.annotations;

import com.elepy.dao.CrudProvider;
import com.elepy.dao.MongoProvider;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
public @interface DaoProvider {

    Class<? extends com.elepy.dao.CrudProvider> value() default MongoProvider.class;
}
