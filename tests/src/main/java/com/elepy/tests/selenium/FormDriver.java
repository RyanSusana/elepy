package com.elepy.tests.selenium;

import com.elepy.models.Model;
import com.elepy.models.Property;
import com.elepy.tests.selenium.actions.FillIn;
import com.elepy.tests.selenium.actions.FillInBoolean;
import com.elepy.tests.selenium.actions.FillInNumber;
import com.elepy.tests.selenium.actions.FillInText;
import org.openqa.selenium.By;

public class FormDriver<T> {

    public static final By SAVE_BUTTON = By.className("save-button");
    private final ElepyDriver driver;
    private final Model<T> model;

    public FormDriver(ElepyDriver driver, Model<T> model) {
        this.driver = driver;
        this.model = model;
    }

    @SuppressWarnings("unchecked")
    public FormDriver<T> fillInField(String propertyName, Object value) {
        final var property = model.getProperty(propertyName);
        getActionFor(property).fillIn(value);
        return this;
    }

    public ModelDriver<T> save() {
        driver.findElement(SAVE_BUTTON).click();

        final By by = By.cssSelector(".uk-notification");

        driver.waitTillCanSee(by);
        driver.findElement(by).click();
        driver.waitTillCantSee(by);
        return new ModelDriver<>(model, driver);
    }

    private FillIn getActionFor(Property property) {
        switch (property.getType()) {
            case TEXT:
                return new FillInText(driver, property);
            case BOOLEAN:
                return new FillInBoolean(driver, property);
            case NUMBER:
                return new FillInNumber(driver, property);
            default:
                throw new IllegalArgumentException(String.format("The property '%s' is not supported", property.getName()));
        }
    }
}
