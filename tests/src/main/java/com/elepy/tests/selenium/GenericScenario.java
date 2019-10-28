package com.elepy.tests.selenium;

import com.elepy.Elepy;

public abstract class GenericScenario {
    protected final ElepyDriver driver;

    public GenericScenario(ElepyDriver driver) {
        this.driver = driver;
    }

    public ElepyDriver driver() {
        return driver;
    }

    public Elepy elepy() {
        return driver.elepy();
    }
}
