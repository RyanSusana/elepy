package com.elepy.tests.selenium.actions;

import com.elepy.schemas.Property;
import com.elepy.tests.selenium.ElepyDriver;
import org.openqa.selenium.By;

public class FillInNumber implements FillIn<Number> {
    private final ElepyDriver driver;
    private final Property property;

    public FillInNumber(ElepyDriver driver, Property property) {
        this.driver = driver;
        this.property = property;
    }

    private By getPropertyField() {
        return By.cssSelector(String.format("*[property=\"%s\"] > :not(div)", property.getName()));
    }

    @Override
    public void fillIn(Number value) {
        driver.findElement(getPropertyField()).sendKeys(value.toString());
    }
}
