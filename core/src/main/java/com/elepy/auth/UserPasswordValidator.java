package com.elepy.auth;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Target({TYPE})
@Retention(RUNTIME)
@Constraint(validatedBy = UserPasswordConstraintValidator.class)
public @interface UserPasswordValidator {

    String message() default "{elepy.models.users.exceptions.password}";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
