package com.aqmp.e2e;

import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.TestWatcher;
import org.openqa.selenium.chrome.ChromeDriver;

import java.util.Optional;

/**
 * Captures a screenshot to target/screenshots whenever a test fails,
 * named after the failing test method for easy correlation in CI reports.
 */
public class ScreenshotOnFailureExtension implements TestWatcher {

    static final ThreadLocal<ChromeDriver> CURRENT_DRIVER = new ThreadLocal<>();

    @Override
    public void testFailed(ExtensionContext context, Throwable cause) {
        ChromeDriver driver = CURRENT_DRIVER.get();
        if (driver != null) {
            BaseTest.saveScreenshot(driver, context.getDisplayName().replaceAll("[^a-zA-Z0-9-_]", "_"));
        }
    }

    public static Optional<ChromeDriver> current() {
        return Optional.ofNullable(CURRENT_DRIVER.get());
    }
}
