package com.elepy.tests.compatibility;

import com.elepy.admin.AdminPanelConfiguration;
import com.elepy.tests.ElepySystemUnderTest;
import com.elepy.tests.selenium.ElepyDriver;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

import java.util.List;
import java.util.Set;


@Tag("e2e")
@Tag("slow")
public class SystemCompatibilityTests extends Stacks {

    private static ChromeDriver chromeDriver;
    private ElepyDriver driver;
    private ElepySystemUnderTest elepySystemUnderTest;


    @ParameterizedTest
    @MethodSource("importantStacks")
    void canLogin(List<String> stack) {

        elepySystemUnderTest
                .addToStack(Stacks.getStack(stack))
                .start();

        driver.createScenario()
                .fromUserLogin("ryan", "susana");


    }

    @BeforeAll
    public static void startBrowser() {
        final var headlessMode = Boolean.parseBoolean(System.getProperty("headlessMode"));
        final var chromeOptions = new ChromeOptions();

        if (headlessMode) {
            chromeOptions.addArguments("--no-sandbox", "--window-size=1920,1000", "disable-infobars", "--disable-extensions", "--disable-gpu", "--disable-dev-shm-usage", "--headless");
        }
        chromeDriver = new ChromeDriver(chromeOptions);
    }

    @BeforeEach
    public void setup() {
        elepySystemUnderTest = ElepySystemUnderTest.create();

        elepySystemUnderTest.addConfiguration(AdminPanelConfiguration.local());

        driver = new ElepyDriver(elepySystemUnderTest, chromeDriver);
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

    public static Set<List<String>> importantStacks() {
        return Stacks.importantStacks();
    }


}
