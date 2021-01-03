package com.elepy.tests.selenium;

import com.elepy.exceptions.ElepyException;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;

import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

import static org.awaitility.Awaitility.await;

public class LoginScenario extends GenericScenario {

    public LoginScenario(ElepyDriver driver) {
        super(driver);
    }

    public HomepageScenario login(String username, String password) {

        driver.navToUrl("/elepy/admin/login");

        driver.findElement(By.name("username")).sendKeys(username);
        driver.findElement(By.name("password")).sendKeys(password);


        driver.findElement(By.id("login-button")).click();

        driver.closeNotifications();
        await().atMost(20, TimeUnit.SECONDS).until(() -> {
            try {
                return driver.findElement(By.cssSelector(".uk-notification")) != null;
            } catch (NoSuchElementException e) {
                return !driver.getCurrentUrl().contains("login");
            }
        });

        if (!driver.getCurrentUrl().contains("login")) {
            return new HomepageScenario(driver);
        } else {
            throw new ElepyException(String.format("Failed to login with '%s:%s'", username, password), 401);
        }
    }

    public LoginScenario custom(Consumer<LoginScenario> consumer) {
        consumer.accept(this);
        return this;
    }
} 
