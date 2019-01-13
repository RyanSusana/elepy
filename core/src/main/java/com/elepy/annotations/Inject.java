package com.elepy.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Use this annotation to annotate dependencies in Handler classes that need to be injected.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD, ElementType.PARAMETER})
public @interface Inject {
    /**
     * @return the context tag. Usually this is empty, but if you have multiple instances.
     * of this object in the {@link com.elepy.di.ElepyContext} then they get differentiated
     * with the tag attribute.
     */
    String tag() default "";

    /**
     * @return The specific classType. Leave blank unless you are working with inheritance trees.
     */
    Class classType() default Object.class;
}
