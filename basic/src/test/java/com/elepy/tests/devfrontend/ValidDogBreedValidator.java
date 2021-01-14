package com.elepy.tests.devfrontend;

import com.elepy.utils.StringUtils;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.Random;

public class ValidDogBreedValidator implements ConstraintValidator<ValidDogBreed, BreedDetail> {

    @Override
    public boolean isValid(BreedDetail value, ConstraintValidatorContext context) {
        return StringUtils.isEmpty(value.getName()) && StringUtils.isEmpty(value.getSize());
    }
}
