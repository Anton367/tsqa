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
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@Tag("ui")
@DisplayName("TC-SRS-002: перелік рахунків і звірка балансу (F-20)")
class AccountsTest {

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
    @DisplayName("TC-SRS-002: перший рахунок в Accounts Overview збігається з деталями рахунку")
    void firstAccountDetailsMatchOverview() {
        driver.get(BASE_URL);
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.name("username"))).sendKeys("john");
        driver.findElement(By.name("password")).sendKeys("demo");
        driver.findElement(By.cssSelector("input[value='Log In']")).click();

        List<WebElement> dataRows = wait.until(d -> {
            List<WebElement> rows = d.findElements(By.cssSelector("#accountTable tbody tr"));
            for (WebElement row : rows) {
                List<WebElement> links = row.findElements(By.cssSelector("td a"));
                List<WebElement> cells = row.findElements(By.tagName("td"));
                if (!links.isEmpty() && cells.size() >= 2 && !cells.get(1).getText().trim().isEmpty()) {
                    return rows;
                }
            }
            return null;
        });

        WebElement firstAccountRow = null;
        for (WebElement row : dataRows) {
            if (!row.findElements(By.cssSelector("td a")).isEmpty()) {
                firstAccountRow = row;
                break;
            }
        }
        assertNotNull(firstAccountRow, "У таблиці #accountTable не знайдено жодного рахунку");

        WebElement firstAccountLink = firstAccountRow.findElements(By.cssSelector("td a")).get(0);
        List<WebElement> cells = firstAccountRow.findElements(By.tagName("td"));
        String expectedAccountId = firstAccountLink.getText().replaceAll("\\s+", "");
        String expectedBalance = cells.get(1).getText().replaceAll("\\s+", "");

        assertAll("Наявність ID та балансу першого рахунку",
                () -> assertFalse(expectedAccountId.isEmpty()),
                () -> assertFalse(expectedBalance.isEmpty())
        );

        wait.until(ExpectedConditions.elementToBeClickable(firstAccountLink)).click();

        wait.until(d -> {
            List<WebElement> idEls = d.findElements(By.id("accountId"));
            List<WebElement> balEls = d.findElements(By.id("balance"));
            return !idEls.isEmpty() && !balEls.isEmpty()
                    && !idEls.get(0).getText().trim().isEmpty()
                    && !balEls.get(0).getText().trim().isEmpty();
        });

        String detailsAccountId = driver.findElement(By.id("accountId")).getText().replaceAll("\\s+", "");
        String detailsBalance = driver.findElement(By.id("balance")).getText().replaceAll("\\s+", "");

        assertAll("Звірка ID і балансу рахунку",
                () -> assertEquals(expectedAccountId, detailsAccountId),
                () -> assertEquals(expectedBalance, detailsBalance)
        );
    }
}