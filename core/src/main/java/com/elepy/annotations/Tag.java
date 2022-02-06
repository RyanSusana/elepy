package com.elepy.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Names an {@link com.elepy.di.ElepyContext} object, can be backtracked for tags
 *
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
public @interface Tag {

    /**
     * @return The name/tag you would like to give a context object.
     */
    String value();
}
