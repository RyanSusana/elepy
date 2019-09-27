package com.elepy.tests.selenium;

import com.elepy.Elepy;
import org.openqa.selenium.WebDriver;

import java.util.function.Consumer;

public abstract class GenericScenario {
    protected final ElepyDriver driver;

    public GenericScenario(ElepyDriver driver) {
        this.driver = driver;
    }

    public WebDriver webDriver() {
        return driver.webDriver();
    }

    public Elepy elepy() {
        return driver.elepy();
    }

    public abstract GenericScenario customFunction(Consumer<ElepyDriver> consumer);

    
}
