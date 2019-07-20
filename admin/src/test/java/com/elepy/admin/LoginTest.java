package com.elepy.admin;

import com.elepy.Elepy;
import com.elepy.auth.User;
import com.elepy.dao.Crud;
import com.elepy.mongo.MongoConfiguration;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mindrot.jbcrypt.BCrypt;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

import static com.google.common.truth.Truth.assertThat;
import static org.awaitility.Awaitility.await;


public class LoginTest {

    private WebDriver driver;
    private Elepy elepy;

    @BeforeEach
    public void startBrowser() {
        final var headlessMode = Boolean.parseBoolean(System.getProperty("headlessMode"));
        final var chromeOptions = new ChromeOptions();

        if (headlessMode) {
            chromeOptions.addArguments("--no-sandbox").addArguments("--headless");
        }
        driver = new ChromeDriver(chromeOptions);

        elepy = new Elepy()
                .addConfiguration(AdminPanel.newAdminPanel())
                .addConfiguration(MongoConfiguration.inMemory())
                .onPort(1339)
                .addModel(Product.class);

        elepy.start();

    }

    @Test
    void createInitialUser() {
        createInitialUser("Ryan", "Susana");
    }

    @Test
    void canCreateUser() {
        createInitialUser("Ryan", "Susana");
        login("Ryan", "Susana");
    }

    private User login(String username, String password) {

        driver.get(path("/elepy-login"));

        driver.findElement(By.name("username")).sendKeys(username);
        driver.findElement(By.name("password")).sendKeys(password);


        driver.findElement(By.id("login-button")).click();

        await().until(() -> driver.getCurrentUrl().contains("admin"));

        return elepy.getCrudFor(User.class).searchInField("username", username).get(0);
    }

    private User createInitialUser(String username, String password) {
        driver.get(path("/admin"));


        driver.findElement(By.name("username")).sendKeys(username);
        driver.findElement(By.name("password")).sendKeys(password);
        driver.findElement(By.name("confirm-password")).sendKeys(password);

        driver.findElement(By.id("login-button")).click();


        final Crud<User> crud = elepy.getCrudFor(User.class);

        await().until(() -> crud.count() == 1);

        assertThat(crud.count()).isEqualTo(1);

        assertThat(crud.getAll().get(0).getUsername())
                .isEqualTo(username);

        assertThat(BCrypt.checkpw(password, crud.getAll().get(0).getPassword()))
                .isTrue();

        return crud.getAll().get(0);
    }


    private String path(String path) {
        return String.format("http://localhost:%d%s", elepy.http().port(), path);
    }

    @AfterEach //has been changed from @After
    public void tearDown() {
        driver.quit();
        elepy.stop();
    }
}
