package com.ryansusana.elepy.admin.annotations;

import com.ryansusana.elepy.admin.concepts.ResourceView;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
public @interface View {
    Class<? extends ResourceView> value();
}
