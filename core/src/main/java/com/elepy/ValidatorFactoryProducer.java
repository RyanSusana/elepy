package com.elepy;

import com.elepy.evaluators.JsonNodeNameProvider;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Produces;
import jakarta.validation.Validation;
import jakarta.validation.ValidatorFactory;
import org.hibernate.validator.HibernateValidator;

@ApplicationScoped
public class ValidatorFactoryProducer {


    @Produces
    @ApplicationScoped
    public  ValidatorFactory getValidatorFactory() {
        return Validation
                .byProvider(HibernateValidator.class)
                .configure()
                .propertyNodeNameProvider(new JsonNodeNameProvider())
                .buildValidatorFactory();
    }
}
