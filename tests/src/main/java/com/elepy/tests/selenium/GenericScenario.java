package com.elepy.tests.selenium;

import com.elepy.Elepy;
import org.openqa.selenium.WebDriver;

public abstract class GenericScenario {
    protected final ElepyDriver driver;

    public GenericScenario(ElepyDriver driver) {
        this.driver = driver;
    }

    public WebDriver driver() {
        return driver.webDriver();
    }

    public Elepy elepy() {
        return driver.elepy();
    }
}
