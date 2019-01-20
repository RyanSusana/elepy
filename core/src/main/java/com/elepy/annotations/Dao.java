package com.elepy.annotations;

import com.elepy.dao.Crud;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * This annotation signifies that this {@link RestModel} uses a custom {@link Crud}
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
public @interface Dao {
    /**
     * @return A reference to the {@link Crud} implementation class
     */
    Class<? extends Crud> value();
}
