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

import java.time.Duration;

import static org.junit.jupiter.api.Assertions.*;

@Tag("ui")
@DisplayName("TC-SRS-001: авторизація з коректними даними та вихід (F-10)")
class LoginTest {

    private static final String BASE_URL = "https://parabank.parasoft.com/parabank/index.htm";

    private WebDriver driver;
    private WebDriverWait wait;

    @BeforeEach
    void setUp() {
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--headless=new", "--window-size=1280,900");
        driver = new ChromeDriver(options);
        wait = new WebDriverWait(driver, Duration.ofSeconds(15));
    }

    @AfterEach
    void tearDown() {
        if (driver != null) {
            driver.quit();
        }
    }

    @Test
    @DisplayName("TC-SRS-001: вхід john/demo відкриває Accounts Overview, Log Out повертає форму входу")
    void loginAndLogoutFlow() {
        driver.get(BASE_URL);

        wait.until(ExpectedConditions.visibilityOfElementLocated(By.name("username"))).sendKeys("john");
        driver.findElement(By.name("password")).sendKeys("demo");
        driver.findElement(By.cssSelector("#loginPanel input[type='submit']")).click();

        String headingAfterLogin = wait.until(
                ExpectedConditions.visibilityOfElementLocated(By.cssSelector("#rightPanel h1"))
        ).getText();
        assertEquals("Accounts Overview", headingAfterLogin);

        WebElement logOutLink = wait.until(
                ExpectedConditions.elementToBeClickable(By.linkText("Log Out"))
        );
        logOutLink.click();

        WebElement loginForm = wait.until(
                ExpectedConditions.visibilityOfElementLocated(By.id("loginPanel"))
        );
        assertAll("Стан після виходу із системи",
                () -> assertTrue(loginForm.isDisplayed()),
                () -> assertTrue(driver.findElement(By.name("username")).isDisplayed()),
                () -> assertTrue(driver.findElement(By.name("password")).isDisplayed())
        );
    }
}