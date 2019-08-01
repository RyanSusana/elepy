package com.elepy.admin.annotations;

import com.elepy.admin.concepts.ModelView;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
public @interface View {
    Class<? extends ModelView> value();
}
