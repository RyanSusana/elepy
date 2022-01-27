package com.elepy.tests.devfrontend;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.ANNOTATION_TYPE;
import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Target({TYPE, ANNOTATION_TYPE})
@Retention(RUNTIME)
@Constraint(validatedBy = {ValidDogBreedValidator.class})
@Documented
public @interface ValidDogBreed {

    String message() default "This woof is invalid!";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
