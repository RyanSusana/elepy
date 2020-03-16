package com.elepy.tests.selenium;

import com.elepy.models.Property;
import com.elepy.models.Schema;
import com.elepy.tests.selenium.actions.FillIn;
import com.elepy.tests.selenium.actions.FillInBoolean;
import com.elepy.tests.selenium.actions.FillInNumber;
import com.elepy.tests.selenium.actions.FillInText;
import org.openqa.selenium.By;

import java.util.Map;
import java.util.function.Consumer;

public class FormInputScenario<T> extends LoggedInScenario {

    public static final By SAVE_BUTTON = By.cssSelector("*[action='save']");
    public static final By YES_BUTTON = By.xpath("//button[contains(., 'Yes')]");
    private final Schema<T> schema;

    public FormInputScenario(ElepyDriver driver, Schema<T> schema) {
        super(driver);
        this.schema = schema;
    }

    @SuppressWarnings("unchecked")
    public FormInputScenario<T> fillInField(String propertyName, Object value) {
        final var property = schema.getProperty(propertyName);

        if (value != null)
            getActionFor(property).fillIn(value);
        return this;
    }

    public FormInputScenario<T> fillInFields(Map<String, Object> fields) {
        fields.forEach(this::fillInField);
        return this;
    }

    @SuppressWarnings("unchecked")
    public FormInputScenario<T> save() {
        driver.findElement(SAVE_BUTTON).click();
        driver.waitTillCanSee(YES_BUTTON);
        driver.findElement(YES_BUTTON).click();
        driver.closeNotifications();
        return this;
    }

    private FillIn getActionFor(Property property) {
        switch (property.getType()) {
            case INPUT:
            case HTML:
            case MARKDOWN:
            case TEXTAREA:
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
