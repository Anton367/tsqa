package ua.kpi.tzyapz.finkit;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Спринт 4. Об'єднані JUnit 5 тести TransferValidator.
 * Авторство кожного тестового методу зазначено безпосередньо перед ним.
 */
@DisplayName("ФінКит — TransferValidator, перевірки BL-40, BL-50, BL-60")
class TransferValidatorTest {

    // Вхідні IBAN з тестів Антона (залишено без змін).
    private static final String ANTON_VALID_FROM_IBAN =
            "UA471111111234567891234567891";
    private static final String ANTON_VALID_TO_IBAN =
            "UA201111111234567891234567892";

    // Вхідні IBAN з тестів Маші (залишено без змін).
    private static final String MASHA_VALID_FROM_IBAN =
            "UA213223130000026007233566001";
    private static final String MASHA_VALID_TO_IBAN =
            "UA903052992990004149123456789";

    private TransferValidator validator;

    @BeforeEach
    void setUp() {
        validator = new TransferValidator();
    }

    // Тести Антона

    // Автор тесту: Антон
    @Test
    @DisplayName("BL-50: При некоректному fromIban INVALID_FROM_IBAN")
    void validate_InvalidFromIban_ReturnsInvalidFromIban() {
        List<TransferError> errors = validator.validate(
                "INVALID_IBAN",
                ANTON_VALID_TO_IBAN,
                new BigDecimal("1000"),
                new BigDecimal("5000"),
                BigDecimal.ZERO
        );

        assertEquals(
                List.of(TransferError.INVALID_FROM_IBAN),
                errors
        );
    }

    // Автор тесту: Антон
    @Test
    @DisplayName("BL-50: При некоректному toIban INVALID_TO_IBAN")
    void validate_InvalidToIban_ReturnsInvalidToIban() {
        List<TransferError> errors = validator.validate(
                ANTON_VALID_TO_IBAN,
                "INVALID_IBAN",
                new BigDecimal("1000"),
                new BigDecimal("5000"),
                BigDecimal.ZERO
        );

        assertEquals(
                List.of(TransferError.INVALID_TO_IBAN),
                errors
        );
    }

    // Автор тесту: Антон
    @ParameterizedTest(name = "BL-60: isValidIban видає true при коректному IBAN: {0}")
    @CsvSource({
            "UA471111111234567891234567891",
            "UA201111111234567891234567892",
    })
    void isValidIban_ValidIban_ReturnsTrue(String iban) {
        assertTrue(TransferValidator.isValidIban(iban));
    }

    // Автор тесту: Антон
    @Test
    @DisplayName("BL-60: isValidIban видає false при зовсім неправильному IBAN")
    void isValidIban_GibberishIban_ReturnsFalse() {
        assertFalse(TransferValidator.isValidIban("INVALID_IBAN"));
    }

    // Автор тесту: Антон
    @Test
    @DisplayName("BL-60: isValidIban видає false при IBAN, що не проходить по MOD-97")
    void isValidIban_WrongMod97_ReturnsFalse() {
        assertFalse(TransferValidator.isValidIban("UA471111111234567891234567892"));
    }

    // Тести Маші

    // Автор тесту: Маша
    @Test
    @DisplayName("BL-40: валідний переказ у межах балансу та добового ліміту не повертає помилок")
    void validTransferReturnsNoErrors() {
        BigDecimal amount = new BigDecimal("2500.50");
        BigDecimal balance = new BigDecimal("10000.00");
        BigDecimal todaySpent = new BigDecimal("1500.00");

        List<TransferError> errors = validator.validate(
                MASHA_VALID_FROM_IBAN, MASHA_VALID_TO_IBAN, amount, balance, todaySpent);

        assertTrue(errors.isEmpty(), "Очікувався порожній список помилок, отримано: " + errors);
    }

    // Автор тесту: Маша
    @ParameterizedTest(name = "BL-40: amount={0} → AMOUNT_NOT_POSITIVE={1}")
    @CsvSource({
            "-100.00, true",
            "0.00,    true",
            "0.01,    false"
    })
    @DisplayName("BL-40: нульова та від'ємна сума переказу відхиляються з AMOUNT_NOT_POSITIVE")
    void nonPositiveAmountBoundary(String amountStr, boolean expectError) {
        BigDecimal amount = new BigDecimal(amountStr);
        BigDecimal balance = new BigDecimal("1000.00");
        BigDecimal todaySpent = BigDecimal.ZERO;

        List<TransferError> errors = validator.validate(
                MASHA_VALID_FROM_IBAN, MASHA_VALID_TO_IBAN, amount, balance, todaySpent);

        assertEquals(expectError, errors.contains(TransferError.AMOUNT_NOT_POSITIVE),
                "Некоректна валідація знаку суми для " + amountStr);
    }

    // Автор тесту: Маша
    @ParameterizedTest(name = "BL-40: amount={0}, balance=5000.00 → INSUFFICIENT_FUNDS={1}")
    @CsvSource({
            "4999.99, false",
            "5000.00, false",
            "5000.01, true"
    })
    @DisplayName("BL-40: переказ понад баланс відхиляється з INSUFFICIENT_FUNDS (перевірка меж)")
    void balanceBoundaryValidation(String amountStr, boolean expectInsufficientFunds) {
        BigDecimal amount = new BigDecimal(amountStr);
        BigDecimal balance = new BigDecimal("5000.00");
        BigDecimal todaySpent = BigDecimal.ZERO;

        List<TransferError> errors = validator.validate(
                MASHA_VALID_FROM_IBAN, MASHA_VALID_TO_IBAN, amount, balance, todaySpent);

        assertEquals(expectInsufficientFunds, errors.contains(TransferError.INSUFFICIENT_FUNDS),
                "Некоректна перевірка достатності коштів для суми " + amountStr);
    }

    // Автор тесту: Маша
    @ParameterizedTest(name = "BL-40: todaySpent={0}, amount={1} → DAILY_LIMIT_EXCEEDED={2}")
    @CsvSource({
            "50000.00,  0.01,     false",
            "99999.99,  0.01,     false",
            "100000.00, 0.01,     true",
            "60000.00,  40000.01, true"
    })
    @DisplayName("BL-40: добовий ліміт переказів становить 100 000 грн згідно з SRS 1.1")
    void dailyLimitIs100000PerSrs11(String spentStr, String amountStr, boolean expectLimitExceeded) {
        BigDecimal todaySpent = new BigDecimal(spentStr);
        BigDecimal amount = new BigDecimal(amountStr);
        BigDecimal balance = new BigDecimal("200000.00");

        List<TransferError> errors = validator.validate(
                MASHA_VALID_FROM_IBAN, MASHA_VALID_TO_IBAN, amount, balance, todaySpent);

        assertEquals(expectLimitExceeded, errors.contains(TransferError.DAILY_LIMIT_EXCEEDED),
                "Очікуваний добовий ліміт за SRS 1.1 — 100 000.00 грн (spent=" + spentStr + ", amount=" + amountStr + ")");
    }

    // Автор тесту: Маша
    @Test
    @DisplayName("BL-40: переказ на той самий рахунок повертає SAME_ACCOUNT")
    void sameFromAndToIbanReturnsSameAccountError() {
        BigDecimal amount = new BigDecimal("500.00");
        BigDecimal balance = new BigDecimal("5000.00");
        BigDecimal todaySpent = BigDecimal.ZERO;

        List<TransferError> errors = validator.validate(
                MASHA_VALID_FROM_IBAN, MASHA_VALID_FROM_IBAN, amount, balance, todaySpent);

        assertAll("Перевірка збігу рахунків",
                () -> assertTrue(errors.contains(TransferError.SAME_ACCOUNT)),
                () -> assertEquals(1, errors.size())
        );
    }
}
