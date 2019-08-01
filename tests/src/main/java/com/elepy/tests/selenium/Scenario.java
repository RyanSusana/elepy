package com.elepy.tests.selenium;

import com.elepy.auth.User;
import com.elepy.dao.Crud;
import com.elepy.models.Model;
import org.mindrot.jbcrypt.BCrypt;
import org.openqa.selenium.By;

import static com.google.common.truth.Truth.assertThat;
import static org.awaitility.Awaitility.await;

public class Scenario {
    private final ElepyDriver driver;


    public Scenario(ElepyDriver driver) {
        this.driver = driver;
    }

    public static Scenario with(ElepyDriver elepyDriver) {
        return new Scenario(elepyDriver);
    }

    public <T> ModelDriver<T> fromModel(Class<T> model) {
        return fromUserLogin("admin", "admin").navigateToModel(model);
    }

    public <T> ModelDriver<T> fromModel(Model<T> model) {
        return fromUserLogin("admin", "admin").navigateToModel(model);
    }

    public MainDriver fromUserLogin(String user, String pass) {
        return fromInitialUser(user, pass).login(user, pass);
    }

    public LoginDriver fromInitialUser(String username, String password) {
        createInialUser(username, password);
        return new LoginDriver(driver);
    }

    public User createInialUser(String username, String password) {
        driver.navToUrl("/elepy-initial-user");

        driver.waitTillCanSee(By.name("username"));

        driver.findElement(By.name("username")).sendKeys(username);
        driver.findElement(By.name("password")).sendKeys(password);
        driver.findElement(By.name("confirm-password")).sendKeys(password);

        driver.findElement(By.id("login-button")).click();


        final Crud<User> crud = driver.elepy().getCrudFor(User.class);

        await().until(() -> crud.count() == 1);

        assertThat(crud.count()).isEqualTo(1);

        assertThat(crud.getAll().get(0).getUsername())
                .isEqualTo(username);

        assertThat(BCrypt.checkpw(password, crud.getAll().get(0).getPassword()))
                .isTrue();

        return crud.getAll().get(0);
    }

}
