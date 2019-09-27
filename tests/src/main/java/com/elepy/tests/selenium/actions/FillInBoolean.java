package com.elepy.tests.selenium.actions;

import com.elepy.models.Property;
import com.elepy.tests.selenium.ElepyDriver;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;

import static org.awaitility.Awaitility.await;

public class FillInBoolean implements FillIn<Boolean> {


    private final ElepyDriver driver;
    private final Property property;

    public FillInBoolean(ElepyDriver driver, Property property) {
        this.driver = driver;
        this.property = property;
    }

    @Override
    public void fillIn(Boolean value) {
        final var input = driver.findElement(getPropertyField(property)).findElement(By.cssSelector(".pad")).findElement(By.tagName("input"));

        if (input.isSelected() != value) {
            input.sendKeys(Keys.SPACE);
            await().until(() -> input.isSelected() == value);
        }
    }


    private By getPropertyField(Property property) {
        return By.cssSelector(String.format("*[property=\"%s\"]", property.getName()));
    }
}
