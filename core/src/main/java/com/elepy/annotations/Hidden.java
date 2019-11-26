package com.elepy.annotations;

import java.lang.annotation.*;

/**
 * Signifies that this value is hidden from Elepy.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD, ElementType.TYPE})
@Inherited
public @interface Hidden {
}
