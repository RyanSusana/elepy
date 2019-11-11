package com.elepy.tests.selenium;

import com.elepy.models.Model;
import com.elepy.models.Property;
import com.elepy.tests.selenium.actions.FillIn;
import com.elepy.tests.selenium.actions.FillInBoolean;
import com.elepy.tests.selenium.actions.FillInNumber;
import com.elepy.tests.selenium.actions.FillInText;
import org.openqa.selenium.By;

import java.util.Map;
import java.util.function.Consumer;

public class FormInputScenario<T> extends LoggedInScenario {

    public static final By SAVE_BUTTON = By.className("save-button");
    private final Model<T> model;

    public FormInputScenario(ElepyDriver driver, Model<T> model) {
        super(driver);
        this.model = model;
    }

    @SuppressWarnings("unchecked")
    public FormInputScenario<T> fillInField(String propertyName, Object value) {
        final var property = model.getProperty(propertyName);

        if (value != null)
            getActionFor(property).fillIn(value);
        return this;
    }

    public FormInputScenario<T> fillInFields(Map<String, Object> fields) {
        fields.forEach(this::fillInField);
        return this;
    }

    @SuppressWarnings("unchecked")
    public ModelScenario<T> save() {
        driver.findElement(SAVE_BUTTON).click();

        driver.closeNotifications();
        return new ModelScenario<>(model, driver);
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

    public FormInputScenario<T> custom(Consumer<FormInputScenario<T>> consumer) {
        consumer.accept(this);
        return this;
    }
}
