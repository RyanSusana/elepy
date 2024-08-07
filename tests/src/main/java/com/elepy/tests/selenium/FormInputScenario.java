package com.elepy.tests.selenium;

import com.elepy.schemas.Property;
import com.elepy.schemas.Schema;
import com.elepy.tests.selenium.actions.FillIn;
import com.elepy.tests.selenium.actions.FillInBoolean;
import com.elepy.tests.selenium.actions.FillInNumber;
import com.elepy.tests.selenium.actions.FillInText;
import org.openqa.selenium.By;

import java.util.Map;
import java.util.function.Consumer;

public class FormInputScenario<T> extends LoggedInScenario {

    public static final By SAVE_BUTTON = By.cssSelector("*[action='save']");
    public static final By YES_BUTTON = By.cssSelector(".uk-modal-dialog .uk-button-primary");
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
        return switch (property.getType()) {
            case INPUT, HTML, MARKDOWN, TEXTAREA -> new FillInText(driver, property);
            case BOOLEAN -> new FillInBoolean(driver, property);
            case NUMBER -> new FillInNumber(driver, property);
            default ->
                    throw new IllegalArgumentException(String.format("The property '%s' with type '%s' is not supported", property.getName(), property.getType()));
        };
    }

    public FormInputScenario<T> custom(Consumer<FormInputScenario<T>> consumer) {
        consumer.accept(this);
        return this;
    }
}
