package com.aqmp.e2e;

import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;

import static org.junit.jupiter.api.Assertions.assertEquals;

/** Journey 3: submitting the data-entry form records a reading and refreshes the dashboard. */
class AddReadingTest extends BaseTest {

    @Test
    void submittingReadingUpdatesStationRow() {
        driver.get(BASE_URL);
        wait.until(ExpectedConditions.presenceOfElementLocated(By.cssSelector("[data-testid='station-table']")));

        Select stationSelect = new Select(driver.findElement(By.cssSelector("[data-testid='station-select']")));
        stationSelect.selectByVisibleText("Hillcrest");

        WebElement aqiInput = driver.findElement(By.cssSelector("[data-testid='aqi-input']"));
        aqiInput.clear();
        aqiInput.sendKeys("65");

        driver.findElement(By.cssSelector("[data-testid='submit-reading']")).click();

        wait.until(d -> d.findElement(By.cssSelector("[data-testid='station-row-4']")).getText().contains("65"));

        WebElement hillcrestRow = driver.findElement(By.cssSelector("[data-testid='station-row-4']"));
        assertEquals(true, hillcrestRow.getText().contains("65"));
    }
}
