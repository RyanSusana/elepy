package com.elepy.tests.selenium.actions;

import com.elepy.models.Property;
import com.elepy.models.options.TextOptions;
import com.elepy.tests.selenium.ElepyDriver;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

public class FillInText implements FillIn<String> {

    private final ElepyDriver driver;
    private final Property property;

    public FillInText(ElepyDriver driver, Property property) {
        this.driver = driver;
        this.property = property;
    }


    private WebElement targetInputElement() {
        final TextOptions options = property.getOptions();

        switch (options.getTextType()) {
            case TEXTAREA:
                return propertyBox().findElement(By.tagName("textarea"));
            case HTML:
            case MARKDOWN:
                return propertyBox().findElement(By.cssSelector("*[contenteditable=\"true\"]"));
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

        targetInputElement().sendKeys(value);
    }

    // i.e click the markdown box before typing,
    // or clicking <code> before filling in html
    private void doPreSetup() {
        final TextOptions options = property.getOptions();

        switch (options.getTextType()) {
            case HTML:
                //click the raw html box
                break;
            case MARKDOWN:
                //click the markdown box
                break;
        }
    }
}
