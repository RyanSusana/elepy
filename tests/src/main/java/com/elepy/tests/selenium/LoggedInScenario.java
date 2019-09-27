package com.elepy.tests.selenium;

import com.elepy.models.Model;

public abstract class LoggedInScenario extends GenericScenario {

    public LoggedInScenario(ElepyDriver driver) {
        super(driver);
    }

    public <T> ModelScenario<T> navigateToModel(Model<T> model) {
        return new ModelScenario<>(model, driver).navigateTo();
    }

    public <T> ModelScenario<T> navigateToModel(Class<T> model) {
        return navigateToModel(driver.elepy().modelFor(model));
    }
} 
