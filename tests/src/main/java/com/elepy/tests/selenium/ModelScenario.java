package com.elepy.tests.selenium;

import com.elepy.models.Model;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.Select;

import java.util.Map;
import java.util.function.Consumer;

import static org.awaitility.Awaitility.await;

public class ModelScenario<T> extends LoggedInScenario {

    private static final By ADD_BUTTON = By.xpath("//*[@id=\"add-button\"]");
    private static final By EDIT_BUTTON = By.xpath("//*[@id=\"edit-button\"]");

    private final Model<T> model;
    private final ElepyDriver driver;

    public ModelScenario(Model<T> model, ElepyDriver driver) {
        super(driver);
        this.model = model;
        this.driver = driver;
    }

    public FormInputScenario<T> startCreating() {
        driver.waitTillCanSee(ADD_BUTTON);
        driver.findElement(ADD_BUTTON).click();
        driver.waitTillCanSee(FormInputScenario.SAVE_BUTTON);
        return new FormInputScenario<>(driver, model);
    }

    public FormInputScenario<T> startEditing(String modelId) {
        driver.findElement(ADD_BUTTON).click();

        final By by = By.cssSelector(String.format("*[row='%s'] *[action='edit']", modelId));
        driver.waitTillCanSee(FormInputScenario.SAVE_BUTTON);
        return new FormInputScenario<>(driver, model);
    }

    public ModelScenario<T> search(String query) {
        return search(query, true);
    }

    public ModelScenario<T> search(String query, boolean expectChanges) {
        driver.findElement(By.id("search-input")).sendKeys(query);

        if (expectChanges) {
            waitUntilChange();
        }

        return this;
    }

    public ModelScenario<T> nextPage(int amountOfTimes) {
        for (int i = 0; i < amountOfTimes; i++) {
            nextPage();
        }
        return this;
    }

    public ModelScenario<T> nextPage() {

        final var initialTable = getTable().getAttribute("innerHTML");
        driver.findElement(By.className("uk-pagination-next")).click();


        if (getCurrentPageNumber() < getLastPageNumber()) {

            waitUntilChange(initialTable);
        }

        return this;
    }

    public ModelScenario<T> previousPage(int amountOfTimes) {
        for (int i = 0; i < amountOfTimes; i++) {
            previousPage();
        }
        return this;
    }

    public ModelScenario<T> previousPage() {

        final var initialTable = getTable().getAttribute("innerHTML");
        driver.findElement(By.className("uk-pagination-previous")).click();

        if (getCurrentPageNumber() > 1) {
            waitUntilChange(initialTable);
        }
        return this;
    }


    public ModelScenario<T> navigateTo() {
        driver.navToUrl("/admin" + model.getSlug());
        return this;
    }

    public ModelScenario<T> changeMaxRowSize(int i) {
        final var select = new Select(driver.findElement(By.className("pagination-select")));

        final var tableHtml = getTableHtml();
        select.selectByValue(String.valueOf(i));

        waitUntilChange(tableHtml);
        return this;
    }

    public ModelScenario<T> create(T item) {
        Map<String, Object> map = new ObjectMapper().convertValue(item, Map.class);

        this.startCreating().fillInFields(map).save();
        return this;
    }


    private int getCurrentPageNumber() {
        return Integer.parseInt(driver.findElement(By.className("pagination-input")).getAttribute("value"));
    }

    public int getRowSize() {
        return getTable().findElements(By.tagName("tr")).size();
    }

    private int getLastPageNumber() {
        final var lastPageNumberWebElement = driver.findElement(By.className("pagination")).findElements(By.tagName("li")).get(2);

        final var pageNumberStrippedDown = lastPageNumberWebElement.getAttribute("innerHTML").replace("of ", "");

        return Integer.parseInt(pageNumberStrippedDown);
    }

    private WebElement getTable() {
        return driver.findElement(By.tagName("tbody"));
    }

    private void waitUntilChange() {
        final var innerHTML = getTable().getAttribute("innerHTML");

        waitUntilChange(innerHTML);
    }

    private void waitUntilChange(String initialHTML) {
        await().until(() -> !initialHTML.equals(getTable().getAttribute("innerHTML")));
    }

    private String getTableHtml() {
        return getTable().getAttribute("innerHTML");
    }


    @Override
    public ModelScenario<T> customFunction(Consumer<ElepyDriver> consumer) {
        consumer.accept(driver);
        return this;
    }
}
