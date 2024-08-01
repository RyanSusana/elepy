package com.elepy.annotations;

import com.elepy.schemas.options.CustomOptions;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.AnnotatedElement;
import java.util.function.Function;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD, ElementType.TYPE_USE})
public @interface Custom {
    Class<? extends Function<AnnotatedElement, CustomOptions>> processor() default CustomOptions.class;

    String scriptLocation() default "";

    Prop[] props() default {};

    @interface Prop {
        String[] value();
    }
}
