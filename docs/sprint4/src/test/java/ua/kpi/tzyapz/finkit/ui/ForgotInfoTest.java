package ua.kpi.tzyapz.finkit.ui;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Зразок UI-тесту Selenium WebDriver. Має тег "ui" і за замовчуванням не запускається.
 * Запуск: mvn test -DexcludedGroups=none   (потрібен встановлений Chrome; драйвер завантажує Selenium Manager)
 */
@Tag("ui")
@DisplayName("TS-SRS-003: сторінка Forgot Info? має зазначені сім полів")
class ForgotInfoTest {

    private WebDriver driver;

    @BeforeEach
    void openBrowser() {
        driver = new FirefoxDriver();
    }

    @AfterEach
    void closeBrowser() {
        if (driver != null) {
            driver.quit();
        }
    }

    boolean isElementPresent(By locator) {
        return !driver.findElements(locator).isEmpty();
    }

    @Test
    @DisplayName("TC-SRS-003: сторінка Forgot Info? має зазначені сім полів")
    void httpsAndPasswordMasking() {
        driver.get("https://parabank.parasoft.com/parabank/lookup.htm");

        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(15));
        assertTrue(isElementPresent(By.id("firstName")));
        assertTrue(isElementPresent(By.id("lastName")));
        assertTrue(isElementPresent(By.id("address.street")));
        assertTrue(isElementPresent(By.id("address.city")));
        assertTrue(isElementPresent(By.id("address.state")));
        assertTrue(isElementPresent(By.id("address.zipCode")));
        assertTrue(isElementPresent(By.id("ssn")));
    }
}