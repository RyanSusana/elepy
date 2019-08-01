package com.elepy.tests.selenium;

import com.elepy.models.Model;

public class MainDriver {
    private final ElepyDriver driver;

    public MainDriver(ElepyDriver driver) {
        this.driver = driver;
    }

    public <T> ModelDriver<T> navigateToModel(Model<T> model) {
        return new ModelDriver<>(model, driver).navToModel();
    }

    public <T> ModelDriver<T> navigateToModel(Class<T> model) {
        return navigateToModel(driver.elepy().modelFor(model));
    }
} 
