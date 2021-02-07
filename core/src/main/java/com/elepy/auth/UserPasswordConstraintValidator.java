package com.elepy.auth;

import com.elepy.utils.StringUtils;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class UserPasswordConstraintValidator implements ConstraintValidator<UserPasswordValidator, User> {
    public void initialize(UserPasswordValidator constraint) {
    }

    public boolean isValid(User user, ConstraintValidatorContext context) {
        if (user.getUsername().contains("@")) {
            return StringUtils.isEmpty(user.getPassword()) || user.getPassword().length() >= 4;
        }
        return !StringUtils.isEmpty(user.getPassword()) && user.getPassword().length() >= 4;
    }
}
