package com.elepy.admin.selenium;

import com.elepy.auth.User;
import org.openqa.selenium.By;

import static org.awaitility.Awaitility.await;

public class LoginDriver {
    private final ElepyDriver driver;

    public LoginDriver(ElepyDriver driver) {
        this.driver = driver;
    }


    public MainDriver login(String username, String password) {

        driver.navToUrl("/elepy-login");

        driver.findElement(By.name("username")).sendKeys(username);
        driver.findElement(By.name("password")).sendKeys(password);


        driver.findElement(By.id("login-button")).click();

        await().until(() -> driver.getCurrentUrl().contains("admin"));

        driver.elepy().getCrudFor(User.class).searchInField("username", username).get(0);

        return new MainDriver(driver);
    }


} 
