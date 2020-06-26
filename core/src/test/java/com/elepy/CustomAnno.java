package com.elepy;

import com.elepy.annotations.ElepyAnnotationsInside;
import com.elepy.annotations.Reference;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
@ElepyAnnotationsInside
@Reference(to = Category.class)
public @interface CustomAnno {

}
