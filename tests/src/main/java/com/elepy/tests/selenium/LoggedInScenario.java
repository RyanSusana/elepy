package com.elepy.tests.selenium;

import com.elepy.models.Schema;
import org.openqa.selenium.By;

public abstract class LoggedInScenario extends GenericScenario {

    public LoggedInScenario(ElepyDriver driver) {
        super(driver);
    }

    public <T> ModelScenario<T> navigateToModel(Schema<T> schema) {
        final var tModelScenario = new ModelScenario<>(schema, driver).navigateTo();

        driver.waitTillCanSee(By.cssSelector(".nav"));
        return tModelScenario;
    }

    public <T> ModelScenario<T> navigateToModel(Class<T> model) {
        return navigateToModel(driver.elepy().modelFor(model));
    }
} 
