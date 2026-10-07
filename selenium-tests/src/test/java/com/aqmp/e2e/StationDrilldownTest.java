package com.aqmp.e2e;

import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;

import static org.junit.jupiter.api.Assertions.assertTrue;

/** Journey 4: clicking a station row opens its reading-history drill-down. */
class StationDrilldownTest extends BaseTest {

    @Test
    void clickingStationOpensHistoryPanel() {
        driver.get(BASE_URL);
        wait.until(ExpectedConditions.presenceOfElementLocated(By.cssSelector("[data-testid='station-row-3']")));

        driver.findElement(By.cssSelector("[data-testid='station-row-3']")).click();

        WebElement panel = wait.until(
                ExpectedConditions.visibilityOfElementLocated(By.cssSelector("[data-testid='drilldown-panel']")));
        assertTrue(panel.getText().contains("Industrial Park"));

        driver.findElement(By.cssSelector("[data-testid='close-drilldown']")).click();
        wait.until(ExpectedConditions.invisibilityOfElementLocated(By.cssSelector("[data-testid='drilldown-panel']")));
    }
}
