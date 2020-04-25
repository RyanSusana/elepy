package com.elepy.tests;

import com.elepy.Elepy;
import com.elepy.admin.AdminPanel;
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

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

@Tag("e2e")
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
            chromeOptions.addArguments("--no-sandbox", "--window-size=1920,1000", "disable-infobars", "--disable-extensions", "--disable-gpu", "--disable-dev-shm-usage", "--headless");
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
                .withPort(counter++)
                .addModel(Settings.class)
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
    void canCreateRecordInSingleMode() throws InterruptedException {
        final var crudFor = elepySystemUnderTest.getCrudFor(Settings.class);
        driver.createScenario()
                .fromUserLogin("user", "password")
                .navigateToModelSingle(Settings.class)
                .fillInField("title", "A title")
                .save()
                .custom(scen -> assertThat(crudFor.count()).isEqualTo(1))
                .fillInField("title", "Another title")
                .save();

        assertThat(crudFor.count())
                .isEqualTo(1);

        assertThat(crudFor.getAll().get(0).getTitle())
                .isEqualTo("Another title");

    }

    @Test
    void createInitialUser() {
        driver.createScenario()
                .fromInitialUser("Username", "Password");
    }

    @Test
    void testWrongPassword() {
        assertThatExceptionOfType(ElepyException.class).isThrownBy(() ->
                driver.createScenario()
                        .fromInitialUser("Username", "Password")
                        .login("Username", "WrongPassword"));
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
                .clickMultiAction("Remove")
                .custom(productModelScenario -> productModelScenario.driver().waitTillCanSee(By.cssSelector(".uk-notification")));

        assertThat(elepySystemUnderTest.getCrudFor(Product.class).count())
                .isEqualTo(1);

    }


    @Test
    void testSingleAction() {
        final var products = seedWithProducts(5);

        driver.createScenario()
                .fromModel(Product.class)
                .clickSingleAction(products.get(0).getId(), "Remove")
                .clickSingleAction(products.get(1).getId(), "Remove2")
                .custom(scenario -> scenario.driver().waitForNotifications(2));


        assertThat(elepySystemUnderTest.getCrudFor(Product.class).count())
                .isEqualTo(3);

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
        seedWithProducts(153);

        final var scenario =
                driver
                        .createScenario()
                        .fromModel(Product.class)


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
                .save()

                .navigateToModel(Product.class);
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
                .fromUserLogin("ryan", "susana")
                .driver()
                .navToUrl("/admin/no-way-josay");

        assertThat(driver.getPageSource()).contains("404");
    }

}
