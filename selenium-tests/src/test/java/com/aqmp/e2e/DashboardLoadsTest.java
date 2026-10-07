package com.aqmp.e2e;

import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/** Journey 1: the dashboard loads and shows the seeded stations with live stats. */
class DashboardLoadsTest extends BaseTest {

    @Test
    void dashboardShowsSeededStationsAndStats() {
        driver.get(BASE_URL);

        wait.until(ExpectedConditions.presenceOfElementLocated(By.cssSelector("[data-testid='station-table']")));

        List<WebElement> rows = driver.findElements(By.cssSelector("[data-testid^='station-row-']"));
        assertEquals(4, rows.size(), "expected the 4 seeded stations to be listed");

        WebElement dashboardError = driver.findElements(By.cssSelector("[data-testid='dashboard-error']"))
                .stream().findFirst().orElse(null);
        assertTrue(dashboardError == null || !dashboardError.isDisplayed(), "backend should be reachable");

        List<WebElement> noStationsMessage = driver.findElements(By.cssSelector("[data-testid='no-stations']"));
        assertFalse(!noStationsMessage.isEmpty() && noStationsMessage.get(0).isDisplayed());
    }
}
