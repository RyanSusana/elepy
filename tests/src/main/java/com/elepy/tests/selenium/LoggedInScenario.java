package com.elepy.tests.selenium;

import com.elepy.models.Model;
import org.openqa.selenium.By;

public abstract class LoggedInScenario extends GenericScenario {

    public LoggedInScenario(ElepyDriver driver) {
        super(driver);
    }

    public <T> ModelScenario<T> navigateToModel(Model<T> model) {
        final var tModelScenario = new ModelScenario<>(model, driver).navigateTo();

        driver.waitTillCanSee(By.cssSelector(".nav"));
        return tModelScenario;
    }

    public <T> ModelScenario<T> navigateToModel(Class<T> model) {
        return navigateToModel(driver.elepy().modelFor(model));
    }
} 
