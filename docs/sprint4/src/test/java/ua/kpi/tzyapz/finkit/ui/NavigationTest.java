package ua.kpi.tzyapz.finkit.ui;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Зразок UI-тесту Selenium WebDriver. Має тег "ui" і за замовчуванням не запускається.
 * Запуск: mvn test -DexcludedGroups=none   (потрібен встановлений Chrome; драйвер завантажує Selenium Manager)
 */
@Tag("ui")
@DisplayName("TC-SRS-017: навігація до 4 зазначених розділів успішна")
class NavigationTest {

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
    @DisplayName("TC-SRS-017: навігація до 4 зазначених розділів успішна")
    void MenuNavigation() {
        driver.get("https://parabank.parasoft.com/parabank/index.htm");
        driver.findElement(By.name("username")).click();
        driver.findElement(By.name("username")).sendKeys("john");
        driver.findElement(By.name("password")).click();
        driver.findElement(By.name("password")).sendKeys("demo");
        driver.findElement(By.cssSelector(".button:nth-child(1)")).click();
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("#showOverview > .title")));
        driver.findElement(By.linkText("Transfer Funds")).click();
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("#showForm > .title")));
        driver.findElement(By.linkText("Request Loan")).click();
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("#requestLoanForm > .title")));
        driver.findElement(By.linkText("Update Contact Info")).click();
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("#updateProfileForm > .title")));
    }
}