package com.elepy.tests.selenium;

import com.elepy.models.Schema;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebElement;

import java.io.Serializable;
import java.util.Map;
import java.util.function.Consumer;

import static org.awaitility.Awaitility.await;

public class ModelScenario<T> extends LoggedInScenario {

    private static final By ADD_BUTTON = By.xpath("//*[@id=\"add-button\"]");
    private static final By YES_BUTTON = By.cssSelector(".uk-modal.uk-open .uk-modal-footer .uk-button-primary");

    private final Schema<T> schema;
    private final ElepyDriver driver;

    public ModelScenario(Schema<T> schema, ElepyDriver driver) {
        super(driver);
        this.schema = schema;
        this.driver = driver;
    }

    public FormInputScenario<T> startCreating() {
        driver.waitTillCanSee(ADD_BUTTON);
        driver.findElement(ADD_BUTTON).click();
        driver.waitTillCanSee(FormInputScenario.SAVE_BUTTON);
        return new FormInputScenario<>(driver, schema);
    }

    public FormInputScenario<T> startEditing(String recordId) {
        clickSingleAction(recordId, "edit");

        driver.waitTillCanSee(FormInputScenario.SAVE_BUTTON);
        return new FormInputScenario<>(driver, schema);
    }

    public ModelScenario<T> delete(Serializable id) {
        return delete(id, false);
    }

    public ModelScenario<T> delete(Serializable id, boolean withSearch) {
        if (withSearch) {
            search(id.toString());
        }

        final var tableHtml = getTableHtml();

        clickSingleAction(id.toString(), "delete");
        confirm();


        waitUntilChange(tableHtml);
        return this;
    }

    public ModelScenario<T> deleteSelected() {
        final var tableHtml = getTableHtml();
        driver.findElement(By.cssSelector(".action-bar *[action=delete]")).click();
        confirm();

        waitUntilChange(tableHtml);
        return this;

    }

    public ModelScenario<T> confirm() {
        driver.waitTillCanSee(YES_BUTTON);
        driver.findElement(YES_BUTTON).click();
        return this;
    }

    public ModelScenario<T> selectRowsById(Iterable<? extends Serializable> ids) {

        for (Serializable id : ids) {
            getTable().findElement(By.cssSelector(String.format("*[row='%s'] input[type=checkbox]", String.valueOf(id)))).sendKeys(Keys.SPACE);
        }
        return this;
    }


    public ModelScenario<T> clickSingleAction(Serializable id, String action) {

        final var actionElementCss = String.format("*[row='%s'] *[action='%s']", id.toString(), action);
        final var element = driver.findElement(By.cssSelector(actionElementCss));

        if (element.isDisplayed()) {
            element.click();
        } else {
            clickSingleAction(id, "select");

            driver.waitTillCanSee(By.cssSelector(actionElementCss));
            clickSingleAction(id, action);
        }
        return this;

    }

    public ModelScenario<T> clickMultiAction(String action) {
        final var actionElementCss = String.format("#multi-actions *[action='%s']", action);
        final var element = driver.findElement(By.cssSelector(actionElementCss));

        if (element.isDisplayed()) {
            element.click();
        } else {
            clickMultiAction("select");

            driver.waitTillCanSee(By.cssSelector(actionElementCss));
            clickMultiAction(action);
        }
        return this;
    }

    public ModelScenario<T> search(String query) {
        return search(query, true);
    }

    public ModelScenario<T> search(String query, boolean expectChanges) {

        final var searchInput = By.id("search-input");
        driver.waitTillCanSee(searchInput);
        final var element = driver.findElement(searchInput);
        element.sendKeys(query);
        element.sendKeys(Keys.ENTER);

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
        driver.findElement(By.cssSelector("#pagination .uk-pagination-next")).click();


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
        driver.findElement(By.cssSelector("#pagination .uk-pagination-previous")).click();

        if (getCurrentPageNumber() > 1) {
            waitUntilChange(initialTable);
        }
        return this;
    }


    public ModelScenario<T> navigateTo() {
        driver.navToUrl("/elepy/admin" + schema.getPath());
        return this;
    }

//    public ModelScenario<T> changeMaxRowSize(int i) {
//        final var select = new Select(driver.findElement(By.className("pagination-select")));
//
//        final var tableHtml = getTableHtml();
//        select.selectByValue(String.valueOf(i));
//
//        waitUntilChange(tableHtml);
//        return this;
//    }

    public ModelScenario<T> create(T item) {
        Map<String, Object> map = new ObjectMapper().convertValue(item, Map.class);

        this.startCreating().fillInFields(map).save();
        return this.navigateTo();
    }


    private int getCurrentPageNumber() {
        return Integer.parseInt(driver.findElement(By.cssSelector("#pagination .current-page")).getAttribute("innerHTML"));
    }

    public int getRowSize() {
        return getTable().findElements(By.tagName("tr")).size();
    }

    public int getLastPageNumber() {
        final var elements = driver.findElements(By.cssSelector("#pagination .last-page"));
        return Integer.parseInt(elements.get(elements.size() - 1).getAttribute("innerHTML"));

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


    public ModelScenario<T> custom(Consumer<ModelScenario<T>> consumer) {
        consumer.accept(this);
        return this;
    }
}
