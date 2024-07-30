package com.elepy.tests.selenium;

import com.elepy.Elepy;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Sequence;
import org.openqa.selenium.remote.RemoteWebDriver;

import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import static org.awaitility.Awaitility.await;

public class ElepyDriver implements WebDriver, org.openqa.selenium.interactions.Interactive {

    private final RemoteWebDriver driver;

    private final Elepy elepy;

    public ElepyDriver(Elepy elepy, RemoteWebDriver driver) {
        this.driver = driver;
        this.elepy = elepy;
    }

    WebDriver webDriver() {
        return driver;
    }

    Elepy elepy() {
        return elepy;
    }

    public Scenarios createScenario() {
        return Scenarios.with(this);
    }

    public void waitTillCanSee(By by) {
        await().atMost(20, TimeUnit.SECONDS).until(() -> {
            try {

                final WebElement element = findElement(by);
                return (element != null && !element.getCssValue("display").equals("none"));
            } catch (NoSuchElementException e) {
                return false;
            }
        });
    }

    public void waitForNotifications(int amount) {
        final By by = By.cssSelector(".uk-notification-message");

        await().until(() -> findElements(by).size() >= amount);
    }
    public void waitForANotification() {
        waitForNotifications(1);
    }

    public void closeNotifications() {
        final By by = By.cssSelector(".uk-notification");


        waitTillCanSee(by);
        findElements(by).forEach(WebElement::click);
    }

    public String path(String path) {
        return String.format("http://localhost:%d%s", elepy().http().port(), path);
    }

    public void navToUrl(String url) {
        get(path(url));
        waitForURL(path(url));
    }

    public void waitForURL(String url) {
        await().atMost(20, TimeUnit.SECONDS).until(() -> getCurrentUrl().contains(url));
    }

    //DELEGATION METHODS

    public void get(String s) {
        driver.get(s);
    }

    public String getCurrentUrl() {
        return driver.getCurrentUrl();
    }

    public String getTitle() {
        return driver.getTitle();
    }

    public List<WebElement> findElements(By by) {
        return driver.findElements(by);
    }

    public WebElement findElement(By by) {
        return driver.findElement(by);
    }

    public String getPageSource() {
        return driver.getPageSource();
    }

    public void close() {
        driver.close();
    }

    public void quit() {
        driver.quit();
    }

    public Set<String> getWindowHandles() {
        return driver.getWindowHandles();
    }

    public String getWindowHandle() {
        return driver.getWindowHandle();
    }

    @Override
    public TargetLocator switchTo() {
        return driver.switchTo();
    }

    @Override
    public Navigation navigate() {
        return driver.navigate();
    }

    @Override
    public Options manage() {
        return driver.manage();
    }


    @Override
    public void perform(Collection<Sequence> actions) {
        driver.perform(actions);
    }

    @Override
    public void resetInputState() {
        driver.resetInputState();
    }
}
