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
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

import static org.junit.jupiter.api.Assertions.*;

@Tag("ui")
@DisplayName("TC-SRS-018: елементи форми відкриття нового рахунку (GUI-45)")
class OpenAccountFormTest {

    private static final String BASE_URL = "https://parabank.parasoft.com/parabank/index.htm";

    private WebDriver driver;
    private WebDriverWait wait;

    @BeforeEach
    void setUp() {
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--headless=new", "--window-size=1280,900");
        driver = new ChromeDriver(options);
        wait = new WebDriverWait(driver, Duration.ofSeconds(20));
    }

    @AfterEach
    void tearDown() {
        if (driver != null) {
            driver.quit();
        }
    }

    @Test
    @DisplayName("TC-SRS-018: форма Open New Account містить списки типу і джерела та кнопку відкриття")
    void openNewAccountFormControlsArePresent() {
        driver.get(BASE_URL);
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.name("username"))).sendKeys("john");
        driver.findElement(By.name("password")).sendKeys("demo");
        driver.findElement(By.cssSelector("input[value='Log In']")).click();

        wait.until(ExpectedConditions.elementToBeClickable(By.linkText("Open New Account"))).click();

        WebElement accountTypeEl = wait.until(
                ExpectedConditions.visibilityOfElementLocated(By.id("type"))
        );
        WebElement fromAccountEl = wait.until(
                ExpectedConditions.visibilityOfElementLocated(By.id("fromAccountId"))
        );
        WebElement submitBtn = wait.until(ExpectedConditions.elementToBeClickable(
                By.cssSelector("input[value='Open New Account'], #openAccountForm input[type='button'], #openAccountForm input[type='submit']")
        ));

        wait.until(d -> !new Select(accountTypeEl).getOptions().isEmpty());
        wait.until(d -> !new Select(fromAccountEl).getOptions().isEmpty());

        Select accountTypeSelect = new Select(accountTypeEl);
        Select fromAccountSelect = new Select(fromAccountEl);

        assertAll("Елементи форми згідно з GUI-45 (без створення рахунку)",
                () -> assertTrue(accountTypeEl.isDisplayed()),
                () -> assertFalse(accountTypeSelect.getOptions().isEmpty()),
                () -> assertTrue(fromAccountEl.isDisplayed()),
                () -> assertFalse(fromAccountSelect.getOptions().isEmpty()),
                () -> assertTrue(submitBtn.isDisplayed()),
                () -> assertTrue(submitBtn.isEnabled())
        );
    }
}