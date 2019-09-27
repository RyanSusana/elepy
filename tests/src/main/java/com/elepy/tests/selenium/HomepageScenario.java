package com.elepy.tests.selenium;

import java.util.function.Consumer;

public class HomepageScenario extends LoggedInScenario {

    public HomepageScenario(ElepyDriver driver) {
        super(driver);
    }

    @Override
    public HomepageScenario customFunction(Consumer<ElepyDriver> consumer) {
        consumer.accept(driver);
        return this;
    }
} 
