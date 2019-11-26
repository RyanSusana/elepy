package com.elepy.annotations;

import com.elepy.models.ModelView;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
@Inherited
public @interface View {
    Class<? extends ModelView> value();
}
