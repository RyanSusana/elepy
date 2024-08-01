package com.elepy.annotations;

import com.elepy.crud.Crud;

import java.lang.annotation.*;

/**
 * This annotation signifies that this {@link Model} uses a custom {@link Crud}
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
@Inherited
public @interface Dao {
    /**
     * @return A reference to the {@link Crud} implementation class
     */
    Class<? extends Crud> value();
}
