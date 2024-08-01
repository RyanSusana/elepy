package com.elepy.tests.selenium;

import com.elepy.auth.User;
import com.elepy.crud.Crud;
import com.elepy.schemas.Schema;
import org.mindrot.jbcrypt.BCrypt;
import org.openqa.selenium.By;

import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;

public class Scenarios {
    private final ElepyDriver driver;


    public Scenarios(ElepyDriver driver) {
        this.driver = driver;
    }

    public static Scenarios with(ElepyDriver elepyDriver) {
        return new Scenarios(elepyDriver);
    }

    public <T> ModelScenario<T> fromModel(Class<T> model) {
        return fromUserLogin("admin", "admin").navigateToModel(model);
    }

    public <T> ModelScenario<T> fromModel(Schema<T> schema) {
        return fromUserLogin("admin", "admin").navigateToModel(schema);
    }

    public HomepageScenario fromUserLogin(String user, String pass) {
        return fromInitialUser(user, pass).login(user, pass);
    }

    public LoginScenario fromInitialUser(String username, String password) {
        createInialUser(username, password);
        return new LoginScenario(driver);
    }

    public User createInialUser(String username, String password) {
        driver.navToUrl("/elepy/admin/initial-user");

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
