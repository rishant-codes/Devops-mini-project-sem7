package com.aqmp.e2e;

import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/** Journey 2: the search box filters the station list by name/location. */
class SearchStationsTest extends BaseTest {

    @Test
    void searchingNarrowsStationListToMatches() {
        driver.get(BASE_URL);
        wait.until(ExpectedConditions.presenceOfElementLocated(By.cssSelector("[data-testid='station-table']")));

        WebElement search = driver.findElement(By.cssSelector("[data-testid='search-input']"));
        search.sendKeys("river");

        wait.until(d -> d.findElements(By.cssSelector("[data-testid^='station-row-']")).size() == 1);

        List<WebElement> rows = driver.findElements(By.cssSelector("[data-testid^='station-row-']"));
        assertEquals(1, rows.size());
        assertTrue(rows.get(0).getText().toLowerCase().contains("riverside"));
    }

    @Test
    void searchingForUnknownStationShowsEmptyState() {
        driver.get(BASE_URL);
        wait.until(ExpectedConditions.presenceOfElementLocated(By.cssSelector("[data-testid='station-table']")));

        WebElement search = driver.findElement(By.cssSelector("[data-testid='search-input']"));
        search.sendKeys("no-such-station-xyz");

        WebElement emptyState = wait.until(
                ExpectedConditions.visibilityOfElementLocated(By.cssSelector("[data-testid='no-stations']")));
        assertTrue(emptyState.isDisplayed());
    }
}
