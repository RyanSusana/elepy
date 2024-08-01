package com.elepy.tests.selenium.actions;

import com.elepy.schemas.FieldType;
import com.elepy.schemas.Property;
import com.elepy.tests.selenium.ElepyDriver;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import static org.awaitility.Awaitility.await;

public class FillInText implements FillIn<String> {

    private final ElepyDriver driver;
    private final Property property;

    public FillInText(ElepyDriver driver, Property property) {
        this.driver = driver;
        this.property = property;
    }


    private WebElement targetInputElement() {

        switch (property.getType()) {
            case TEXTAREA:
                return propertyBox().findElement(By.tagName("textarea"));
            case HTML:
            case MARKDOWN:
                return propertyBox().findElement(By.cssSelector("*[contenteditable=\"true\"]"));

            case INPUT:
                return propertyBox().findElement(By.tagName("input"));
            default:
                return propertyBox().findElement(By.cssSelector(":not(div)"));
        }
    }

    private WebElement propertyBox() {
        return driver.findElement(By.cssSelector(String.format("*[property=\"%s\"]", property.getName())));
    }

    @Override
    public void fillIn(String value) {
        doPreSetup();

        final var webElement = targetInputElement();
        if (property.getType().equals(FieldType.INPUT)) {
            webElement.clear();
        }
        webElement.sendKeys(value);
    }

    private void doPreSetup() {

        if (property.getType() == FieldType.MARKDOWN) {//click the markdown box
            final var compiledMarkdown = propertyBox().findElement(By.cssSelector(".compiled-markdown"));
            compiledMarkdown.click();

            await().until(() -> compiledMarkdown.getCssValue("display").equals("none"));
        }
    }
}
