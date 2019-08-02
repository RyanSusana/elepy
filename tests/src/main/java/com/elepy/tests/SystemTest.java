package com.elepy.tests;

import com.elepy.Elepy;
import com.elepy.admin.AdminPanel;
import com.elepy.tests.selenium.ElepyDriver;
import com.elepy.tests.selenium.Scenario;
import org.junit.jupiter.api.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

import java.math.BigDecimal;

import static com.google.common.truth.Truth.assertThat;

public abstract class SystemTest implements ElepyConfigHelper {

    private ElepyDriver driver;
    private Elepy elepySystemUnderTest;

    private static int counter = 1339;
    private static ChromeDriver chromeDriver;

    @BeforeAll
    public static void startBrowser() {
        final var headlessMode = Boolean.parseBoolean(System.getProperty("headlessMode"));
        final var chromeOptions = new ChromeOptions();

        if (headlessMode) {
            chromeOptions.addArguments("--disable-gpu").addArguments("--no-sandbox").addArguments("--headless").addArguments("--window-size=2120,1280");
        }
        chromeDriver = new ChromeDriver(chromeOptions);
    }

    @AfterAll
    public static void afterAll() {

        chromeDriver.close();
        chromeDriver.quit();
    }

    @AfterEach
    public void tearDown() {
        elepySystemUnderTest.stop();
    }

    @BeforeEach
    public void setup() {
        elepySystemUnderTest = new Elepy();

        this.configureElepy(elepySystemUnderTest);


        elepySystemUnderTest.addConfiguration(AdminPanel.newAdminPanel())

                .onPort(counter++)
                .addModel(CantSeeThis.class)
                .addModel(Product.class);

        elepySystemUnderTest.start();

        driver = new ElepyDriver(elepySystemUnderTest, chromeDriver);

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


    @Test()
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
        assertThat(products.getAll().get(0).getPrice().intValue())
                .isEqualTo(200);
    }

    @Test
    void testCantSeeNonViewableModel() throws InterruptedException {

        Scenario
                .with(driver)
                .fromModel(CantSeeThis.class);

        assertThat(driver.getPageSource()).contains("404");

    }

}
