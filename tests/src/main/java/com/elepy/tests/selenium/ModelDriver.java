package com.elepy.tests.selenium;

import com.elepy.models.Model;
import org.openqa.selenium.By;

public class ModelDriver<T> {

    private static final By ADD_BUTTON = By.xpath("//*[@id=\"add-button\"]");
    private static final By EDIT_BUTTON = By.xpath("//*[@id=\"edit-button\"]");

    private final Model<T> model;
    private final ElepyDriver driver;

    public ModelDriver(Model<T> model, ElepyDriver driver) {
        this.model = model;
        this.driver = driver;
    }

    public FormDriver<T> startCreating() {
        driver.waitTillCanSee(ADD_BUTTON);
        driver.findElement(ADD_BUTTON).click();
        driver.waitTillCanSee(FormDriver.SAVE_BUTTON);
        return new FormDriver<>(driver, model);
    }

    public FormDriver<T> startEditing(String modelId) {
        driver.findElement(ADD_BUTTON).click();

        final By by = By.cssSelector(String.format("*[row='%s'] *[action='edit']", modelId));
        driver.waitTillCanSee(FormDriver.SAVE_BUTTON);
        return new FormDriver<>(driver, model);
    }


    public ModelDriver<T> navToModel() {
        driver.navToUrl("/admin" + model.getSlug());
        return this;
    }

}
