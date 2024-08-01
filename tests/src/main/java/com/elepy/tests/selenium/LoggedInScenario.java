package com.elepy.tests.selenium;

import com.elepy.schemas.Schema;
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


    public <T> FormInputScenario<T> navigateToModelSingle(Schema<T> schema) {
        driver.navToUrl("/elepy/admin" + schema.getPath());
        driver.waitTillCanSee(By.cssSelector(".nav"));

        return new FormInputScenario<T>(driver, schema);
    }

    public <T> ModelScenario<T> navigateToModel(Class<T> model) {
        return navigateToModel(driver.elepy().modelSchemaFor(model));
    }

    public <T> FormInputScenario<T> navigateToModelSingle(Class<T> model) {
        return navigateToModelSingle(driver.elepy().modelSchemaFor(model));
    }
} 
