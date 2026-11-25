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
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.net.URI;
import java.time.Duration;

import static org.junit.jupiter.api.Assertions.assertEquals;

/** Sprint 4, author: Liza (KT), TC-SRS-028 / S-20. */
@Tag("ui")
class SecurityUiTest {
    private WebDriver driver;

    @BeforeEach
    void openBrowser() {
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--headless=new", "--window-size=1280,900");
        // Do NOT set acceptInsecureCerts=true; certificate errors should not be bypassed.
        driver = new ChromeDriver(options);
    }

    @AfterEach
    void closeBrowser() {
        if (driver != null) {
            driver.quit();
        }
    }

    @Test
    @DisplayName("TC-SRS-028 / S-20: сторінка HTTPS і поле пароля маскує введення")
    void httpsAndPasswordMasking() {
        driver.get("https://parabank.parasoft.com/parabank/index.htm");

        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(15));
        WebElement password = wait.until(
                ExpectedConditions.visibilityOfElementLocated(By.name("password")));

        assertEquals("https", URI.create(driver.getCurrentUrl()).getScheme());
        assertEquals("password", password.getDomAttribute("type"));

        password.sendKeys("demo");
        assertEquals("demo", password.getDomProperty("value"));
    }
}
