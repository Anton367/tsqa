package ua.kpi.tzyapz.finkit;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * Sprint 4, author: Liza (KT), target: LoanCalculator v1.
 * A failing test is kept red and linked to a confirmed module Issue.
 */
class LoanCalculatorTest {
    private final LoanCalculator calculator = new LoanCalculator();

    @ParameterizedTest
    @CsvSource({
            "100000, 12, 12, 8884.88",
            "100000, 11.99, 12, 8884.41"
    })
    @DisplayName("BL-10: щомісячний ануїтетний платіж без передчасного округлення ставки")
    void monthlyPaymentUsesFullRatePrecision(String principal, String rate, int months, String expected) {
        BigDecimal actual = calculator.monthlyPayment(
                new BigDecimal(principal), new BigDecimal(rate), months);
        assertEquals(new BigDecimal(expected), actual);
    }

    @Test
    @DisplayName("BL-20: переплата = округлений платіж × строк − тіло кредиту")
    void totalInterestMatchesPaymentSchedule() {
        BigDecimal actual = calculator.totalInterest(
                new BigDecimal("100000"), new BigDecimal("12"), 12);
        assertEquals(new BigDecimal("6618.56"), actual);
    }

    @ParameterizedTest
    @ValueSource(strings = {"1000", "1000000"})
    @DisplayName("BL-30: мінімальна і максимальна суми кредиту допустимі")
    void principalBoundariesAreAccepted(String principal) {
        assertDoesNotThrow(() -> calculator.monthlyPayment(
                new BigDecimal(principal), new BigDecimal("12"), 12));
    }

    @ParameterizedTest
    @CsvSource({
            "999.99, 12, 12",
            "1000000.01, 12, 12",
            "100000, 12, 2",
            "100000, 12, 61",
            "100000, 4.99, 12",
            "100000, 40.01, 12"
    })
    @DisplayName("BL-30: сума/строк поза межами та ставка поза контрактом відхиляються")
    void invalidLoanParametersAreRejected(String principal, String rate, int months) {
        assertThrows(IllegalArgumentException.class, () -> calculator.monthlyPayment(
                new BigDecimal(principal), new BigDecimal(rate), months));
    }

    @ParameterizedTest
    @ValueSource(ints = {3, 60})
    @DisplayName("BL-30: строки 3 і 60 місяців допустимі")
    void loanTermBoundariesAreAccepted(int months) {
        assertDoesNotThrow(() -> calculator.monthlyPayment(
                new BigDecimal("100000"), new BigDecimal("12"), months));
    }
}
