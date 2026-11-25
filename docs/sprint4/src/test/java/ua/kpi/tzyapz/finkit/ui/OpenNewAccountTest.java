package ua.kpi.tzyapz.finkit.ui;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.net.URI;
import java.time.Duration;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Зразок UI-тесту Selenium WebDriver. Має тег "ui" і за замовчуванням не запускається.
 * Запуск: mvn test -DexcludedGroups=none   (потрібен встановлений Chrome; драйвер завантажує Selenium Manager)
 */
@Tag("ui")
@DisplayName("Smoke: головна сторінка demo-застосунку відкривається")
class OpenNewAccountTest {

    private WebDriver driver;
    private WebDriverWait wait;

    @BeforeEach
    void openBrowser() {
        driver = new FirefoxDriver();
        wait = new WebDriverWait(driver, Duration.ofSeconds(15));
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
    @DisplayName("TC-SRS-003: сторінка HTTPS і поле пароля маскує введення")
    void httpsAndPasswordMasking() {
        driver.get("https://parabank.parasoft.com/parabank/index.htm");
        driver.findElement(By.name("username")).click();
        driver.findElement(By.name("username")).sendKeys("john");
        driver.findElement(By.name("password")).click();
        driver.findElement(By.name("password")).sendKeys("demo");
        driver.findElement(By.cssSelector(".button:nth-child(1)")).click();
        WebElement create_new_link = wait.until(
                ExpectedConditions.visibilityOfElementLocated(By.linkText("Open New Account")));
        create_new_link.click();
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("#openAccountForm > .title")));
        assertTrue(isElementPresent(By.id("type")));
        assertTrue(isElementPresent(By.id("fromAccountId")));
    }
}