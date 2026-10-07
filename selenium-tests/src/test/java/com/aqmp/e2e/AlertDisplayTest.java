package com.aqmp.e2e;

import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;

import static org.junit.jupiter.api.Assertions.assertTrue;

/** Journey 5: a station above the AQI alert threshold (100) surfaces in the alerts panel. */
class AlertDisplayTest extends BaseTest {

    @Test
    void stationAboveThresholdAppearsInAlertsPanel() {
        driver.get(BASE_URL);

        WebElement panel = wait.until(
                ExpectedConditions.visibilityOfElementLocated(By.cssSelector("[data-testid='alerts-panel']")));

        // Seed data: Industrial Park reports AQI 154, which is above the 100 threshold.
        assertTrue(panel.getText().contains("Industrial Park"), "expected Industrial Park alert to be listed");
        assertTrue(panel.getText().contains("154"));
    }
}
