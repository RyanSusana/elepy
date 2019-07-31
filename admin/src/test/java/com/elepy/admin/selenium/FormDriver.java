package com.elepy.admin.selenium;

import com.elepy.models.FieldType;
import com.elepy.models.Model;
import com.elepy.models.Property;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import java.util.List;

public class FormDriver<T> {

    public static final By SAVE_BUTTON = By.className("save-button");
    private final ElepyDriver driver;
    private final Model<T> model;

    public FormDriver(ElepyDriver driver, Model<T> model) {
        this.driver = driver;
        this.model = model;
    }

    public FormDriver<T> fillInField(String propertyName, Object text) {
        findProperty(propertyName).sendKeys(text.toString());
        return this;
    }

    public WebElement findProperty(String propertyName) {
        return driver.findElement(getPropertyField(propertyName));
    }

    public List<WebElement> findProperties(String propertyName) {
        return driver.findElements(getPropertyField(propertyName));
    }

    public By getPropertyField(Property property) {
        return By.cssSelector(String.format("*[property=\"%s\"] > :not(div)", property.getName()));
    }

    protected By getPropertyField(String propertyName) {
        return getPropertyField(model.getProperty(propertyName));
    }

    public ModelDriver<T> save() {
        driver.findElement(SAVE_BUTTON).click();

        final By by = By.cssSelector(".uk-notification");

        driver.waitTillCanSee(by);
        driver.findElement(by).click();
        driver.waitTillCantSee(by);
        return new ModelDriver<>(model, driver);
    }

    private boolean isPropertySupported(Property property) {
        return List.of(FieldType.TEXT, FieldType.NUMBER).contains(property.getType());
    }
}
