package com.aqmp.e2e;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;

/**
 * Shared Selenium setup for all AQMP end-to-end journeys.
 *
 * Requires the backend (default http://localhost:8080) and the frontend dev
 * server (default http://localhost:5173) to already be running. Override
 * with -Dbase.url=... -Dapi.url=... on the Maven command line.
 */
@ExtendWith(ScreenshotOnFailureExtension.class)
public abstract class BaseTest {

    protected static final String BASE_URL = System.getProperty("base.url", "http://localhost:5173");
    protected static final String API_URL = System.getProperty("api.url", "http://localhost:8080");

    protected ChromeDriver driver;
    protected WebDriverWait wait;

    @BeforeEach
    void setUpDriver() {
        WebDriverManager.chromedriver().setup();
        ChromeOptions options = new ChromeOptions();
        if (Boolean.parseBoolean(System.getProperty("headless", "true"))) {
            options.addArguments("--headless=new");
        }
        options.addArguments("--window-size=1366,900");
        driver = new ChromeDriver(options);
        wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        ScreenshotOnFailureExtension.CURRENT_DRIVER.set(driver);
    }

    @AfterEach
    void tearDownDriver() {
        if (driver != null) {
            driver.quit();
        }
    }

    static void saveScreenshot(ChromeDriver driver, String testName) {
        try {
            Path dir = Path.of("target", "screenshots");
            Files.createDirectories(dir);
            File src = driver.getScreenshotAs(OutputType.FILE);
            Files.copy(src.toPath(), dir.resolve(testName + ".png"));
        } catch (Exception ignored) {
            // Screenshot capture is best-effort; never fail the test because of it.
        }
    }
}
