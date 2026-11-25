package ua.kpi.tzyapz.finkit;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("ФінКит — зразки юніт-тестів модуля")
class TransferValidatorTest {

    private TransferValidator validator;

    private static final String VALID_FROM_IBAN =
            "UA471111111234567891234567891";
    private static final String VALID_TO_IBAN =
            "UA201111111234567891234567892";

    @BeforeEach
    void setUp() {
        validator = new TransferValidator();
    }

    @Test
    @DisplayName("BL-50: При некоректному fromIban INVALID_FROM_IBAN")
    void validate_InvalidFromIban_ReturnsInvalidFromIban() {
        List<TransferError> errors = validator.validate(
                "INVALID_IBAN",
                VALID_TO_IBAN,
                new BigDecimal("1000"),
                new BigDecimal("5000"),
                BigDecimal.ZERO
        );

        assertEquals(
                List.of(TransferError.INVALID_FROM_IBAN),
                errors
        );
    }

    @Test
    @DisplayName("BL-50: При некоректному toIban INVALID_TO_IBAN")
    void validate_InvalidToIban_ReturnsInvalidToIban() {
        List<TransferError> errors = validator.validate(
                VALID_TO_IBAN,
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

    @ParameterizedTest(name = "BL-60: isValidIban видає true при коректному IBAN: {0}")
    @CsvSource({
            "UA471111111234567891234567891",
            "UA201111111234567891234567892",
    })
    void isValidIban_ValidIban_ReturnsTrue(String iban) {
        assertTrue(TransferValidator.isValidIban(iban));
    }

    @Test
    @DisplayName("BL-60: isValidIban видає false при зовсім неправильному IBAN")
    void isValidIban_GibberishIban_ReturnsFalse() {
        assertFalse(TransferValidator.isValidIban("INVALID_IBAN"));
    }

    @Test
    @DisplayName("BL-60: isValidIban видає false при IBAN, що не проходить по MOD-97")
    void isValidIban_WrongMod97_ReturnsFalse() {
        assertFalse(TransferValidator.isValidIban("UA471111111234567891234567892"));
    }
}