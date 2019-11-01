package com.elepy.tests;

import com.auth0.jwt.algorithms.Algorithm;
import com.elepy.Elepy;
import com.elepy.admin.AdminPanel;
import com.elepy.auth.methods.JWTAuthenticationMethod;
import com.elepy.exceptions.ElepyException;
import com.elepy.tests.selenium.ElepyDriver;
import com.elepy.tests.selenium.ModelScenario;
import com.elepy.tests.selenium.Scenarios;
import org.junit.jupiter.api.*;
import org.openqa.selenium.By;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

import java.math.BigDecimal;
import java.util.Calendar;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static com.google.common.truth.Truth.assertThat;
import static com.google.common.truth.Truth8.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

public abstract class SystemTest implements ElepyConfigHelper {

    private static int counter = 1339;
    private static ChromeDriver chromeDriver;
    private ElepyDriver driver;
    private Elepy elepySystemUnderTest;

    @BeforeAll
    public static void startBrowser() {
        final var headlessMode = Boolean.parseBoolean(System.getProperty("headlessMode"));
        final var chromeOptions = new ChromeOptions();

        if (headlessMode) {
            chromeOptions.addArguments("--no-sandbox", "start-maximized", "disable-infobars", "--disable-extensions", "--disable-gpu", "--disable-dev-shm-usage", "--headless");
        }
        chromeDriver = new ChromeDriver(chromeOptions);
    }

    @AfterAll
    public static void afterAll() {
        chromeDriver.quit();
        chromeDriver = null;
    }

    @AfterEach
    public void tearDown() {
        elepySystemUnderTest.stop();
        chromeDriver.manage().deleteAllCookies();
    }

    @BeforeEach
    public void setup() {
        elepySystemUnderTest = new Elepy();

        this.configureElepy(elepySystemUnderTest);

        elepySystemUnderTest.addConfiguration(AdminPanel.local())

                .addAuthenticationMethod(new JWTAuthenticationMethod(Algorithm.HMAC256("secret")))
                .onPort(counter++)
                .addModel(CantSeeThis.class)
                .addModel(Product.class);

        elepySystemUnderTest.start();

        driver = new ElepyDriver(elepySystemUnderTest, chromeDriver);

    }

    public ElepyDriver driver() {
        return driver;
    }

    @Test
    void canCreateUser() {
        driver.createScenario()
                .fromUserLogin("Ryan", "Susana");


    }

    @Test
    void createInitialUser() {
        driver.createScenario()
                .fromInitialUser("Username", "Password");
    }

    @Test
    void testWrongPassword() {
        assertThrows(ElepyException.class, () ->
                driver.createScenario()
                        .fromInitialUser("Username", "Password")
                        .login("Username", "WrongPassword")
        );
    }


    @Test
    void testProductDeleteSingle() {
        final var productIds = seedWithProducts(5).stream().map(Product::getId).collect(Collectors.toList());

        final var toDelete = productIds.get(0);
        var products = elepySystemUnderTest.getCrudFor(Product.class);
        driver.createScenario()
                .fromModel(Product.class)
                .delete(toDelete);

        assertThat(products.count())
                .isEqualTo(4);

        assertThat(products.getById(toDelete))
                .isEmpty();
    }

    @Test
    void testProductDeleteMultiple() {
        final var productIds = seedWithProducts(5).stream().map(Product::getId).collect(Collectors.toList());

        productIds.remove(1);

        var products = elepySystemUnderTest.getCrudFor(Product.class);
        driver.createScenario()
                .fromModel(Product.class)
                .selectRowsById(productIds)
                .deleteSelected();

        assertThat(products.count())
                .isEqualTo(1);

    }


    @Test
    void testProductSave() {
        var products = elepySystemUnderTest.getCrudFor(Product.class);
        productCreationScenario("This is a short description");


        assertThat(products.count())
                .isEqualTo(1);

        final var product = products.getAll().get(0);

        assertThat(product.getPrice().intValue())
                .isEqualTo(200);

        assertThat(product.isProductIsAwesome())
                .isTrue();

        assertThat(product.getHtmlDescription())
                .isEqualTo("<p>This is a long description</p>");

        assertThat(product.getShortDescription())
                .isEqualTo("This is a short description");

        assertThat(product.getMarkdown())
                .isEqualTo("This is markdown");

    }

    @Test
    void testMultiAction() {
        final var products = seedWithProducts(5);

        //Select all but index one
        products.remove(1);

        driver.createScenario()
                .fromModel(Product.class)
                .selectRowsById(products.stream().map(Product::getId).collect(Collectors.toList()))
                .clickMultiAction("Delete All")
                .custom(productModelScenario -> productModelScenario.driver().waitTillCanSee(By.cssSelector(".uk-notification")));

        assertThat(elepySystemUnderTest.getCrudFor(Product.class).count())
                .isEqualTo(1);

    }

    @Test
    void testProductSearch() {
        seedWithProducts(80);

        final var product = new Product();

        product.setShortDescription("Should find one too");
        final var productScenario = productCreationScenario("Should find one")
                .create(product)
                .search("Should find one");

        assertThat(productScenario.getRowSize())
                .isEqualTo(2);
    }

    @Test
    void testPagination() {
        seedWithProducts(63);

        final var scenario =
                driver
                        .createScenario()
                        .fromModel(Product.class)
                        .changeMaxRowSize(10)

                        //Make sure that the last page remains consistent
                        .custom(scen -> IntStream.range(0, 6).forEach(i -> {
                                    assertThat(scen.getLastPageNumber())
                                            .isEqualTo(7);

                                    scen.nextPage();
                                })
                        )
                        .previousPage()
                        .nextPage();

        assertThat(scenario.getRowSize())
                .isEqualTo(3);

    }

    private ModelScenario<Product> productCreationScenario(String shortDescription) {
        return driver
                .createScenario()
                .fromUserLogin("username", "password")
                .navigateToModel(Product.class)
                .startCreating()
                .fillInField("price", BigDecimal.valueOf(200.00))
                .fillInField("productIsAwesome", true)
                .fillInField("shortDescription", shortDescription)
                .fillInField("htmlDescription", "This is a long description")
                .fillInField("markdown", "This is markdown")
                .save();
    }

    private List<Product> seedWithProducts(int amount) {

        final var seededProducts = IntStream.range(1, amount + 1).mapToObj(
                i -> {
                    Product product = new Product();

                    product.setId(i);
                    product.setShortDescription("Seeded product " + i);
                    product.setDate(Calendar.getInstance().getTime());
                    return product;
                }
        ).collect(Collectors.toList());

        elepySystemUnderTest.getCrudFor(Product.class).create(seededProducts);


        return seededProducts;
    }

    @Test
    void testCantSeeNonViewableModel() {

        Scenarios
                .with(driver)
                .fromModel(CantSeeThis.class);

        assertThat(driver.getPageSource()).contains("404");

    }

}
