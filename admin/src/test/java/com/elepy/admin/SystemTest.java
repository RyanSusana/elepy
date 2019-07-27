package com.elepy.admin;

import com.elepy.Elepy;
import com.elepy.admin.selenium.ElepyDriver;
import com.elepy.admin.selenium.Scenario;
import com.elepy.describers.Model;
import com.elepy.mongo.MongoConfiguration;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

import java.math.BigDecimal;

import static com.google.common.truth.Truth.assertThat;

public class SystemTest {

    private ElepyDriver driver;
    private Elepy elepySystemUnderTest;
    private Model<Product> model;

    @BeforeEach
    public void startBrowser() {
        final var headlessMode = Boolean.parseBoolean(System.getProperty("headlessMode"));
        final var chromeOptions = new ChromeOptions();

        if (headlessMode) {
            chromeOptions.addArguments("--no-sandbox").addArguments("--headless");
        }
        elepySystemUnderTest = new Elepy()
                .addConfiguration(AdminPanel.newAdminPanel())
                .addConfiguration(MongoConfiguration.inMemory())
                .onPort(1339)
                .addModel(Product.class);

        elepySystemUnderTest.start();

        model = elepySystemUnderTest.getModelDescriptionFor(Product.class);


        driver = new ElepyDriver(elepySystemUnderTest, new ChromeDriver(chromeOptions));

    }


    @Test
    void canCreateUser() {
        Scenario
                .with(driver)
                .fromUserLogin("Ryan", "Susana");
    }

    @Test
    void createInitialUser() {

        Scenario
                .with(driver)
                .fromInitialUser("Username", "Password");
    }


    @Test
    void testProductSave() {
        var products = elepySystemUnderTest.getCrudFor(Product.class);

        Scenario
                .with(driver)
                .fromUserLogin("username", "password")

                //Navigate to  /products
                .navigateToModel(Product.class)

                .startCreating()
                .fillInField("price", BigDecimal.valueOf(200.00))
                .save();


        assertThat(products.count())
                .isEqualTo(1);
        assertThat(products.getAll().get(0).getPrice())
                .isEqualTo(BigDecimal.valueOf(200.0));
    }


    @AfterEach
    public void tearDown() {
        driver.close();
        driver.quit();
        elepySystemUnderTest.stop();
    }
}
