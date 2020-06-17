package com.elepy.annotations;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
@Inherited
public @interface View {

    String value() default Defaults.DEFAULT;

    class Defaults {
        public static final String DEFAULT = "default", SINGLE = "single";
    }


}
