package ua.kpi.tzyapz.finkit;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("AccountLedger — облік коштів, овердрафт та історія операцій (BL-70, BL-80)")
class AccountLedgerTest {

    private AccountLedger ledger;

    @BeforeEach
    void setUp() {
        ledger = new AccountLedger();
    }

    @Test
    @DisplayName("BL-70: поповнення збільшує баланс зі шкалою до копійок")
    void depositIncreasesBalanceWithTwoDecimals() {
        BigDecimal amount = new BigDecimal("150.5");

        ledger.deposit(amount);

        assertEquals(new BigDecimal("150.50"), ledger.balance());
    }

    @ParameterizedTest(name = "BL-70: сума {0} кидає IllegalArgumentException")
    @ValueSource(strings = {"0", "0.00", "-25.50"})
    void nonPositiveAmountThrowsIllegalArgument(String rawAmount) {
        BigDecimal amount = new BigDecimal(rawAmount);

        assertAll("Нульові та від'ємні суми для deposit і withdraw",
                () -> assertThrows(IllegalArgumentException.class, () -> ledger.deposit(amount)),
                () -> assertThrows(IllegalArgumentException.class, () -> ledger.withdraw(amount))
        );
    }

    @Test
    @DisplayName("BL-70: зняття всієї суми балансу залишає 0.00 без овердрафту")
    void withdrawFullBalanceLeavesZero() {
        ledger.deposit(new BigDecimal("300.00"));

        ledger.withdraw(new BigDecimal("300.00"));

        assertEquals(new BigDecimal("0.00"), ledger.balance());
    }

    @Test
    @DisplayName("BL-70: списання понад баланс кидає IllegalStateException і не змінює баланс")
    void withdrawAboveBalanceThrowsIllegalState() {
        ledger.deposit(new BigDecimal("100.00"));

        assertAll("Відмова при недостатніх коштах",
                () -> assertThrows(IllegalStateException.class, () -> ledger.withdraw(new BigDecimal("100.01"))),
                () -> assertEquals(new BigDecimal("100.00"), ledger.balance())
        );
    }

    @Test
    @DisplayName("BL-80: історія зберігає порядок виконання операцій і баланс після кожної")
    void historyPreservesExecutionOrderAndBalances() {
        ledger.deposit(new BigDecimal("500.00"));
        ledger.withdraw(new BigDecimal("120.50"));

        List<LedgerEntry> history = ledger.history();

        assertAll("Перевірка послідовності записів історії",
                () -> assertEquals(2, history.size()),
                () -> assertEquals(LedgerEntry.Type.DEPOSIT, history.get(0).type()),
                () -> assertEquals(new BigDecimal("500.00"), history.get(0).balanceAfter()),
                () -> assertEquals(LedgerEntry.Type.WITHDRAWAL, history.get(1).type()),
                () -> assertEquals(new BigDecimal("379.50"), history.get(1).balanceAfter())
        );
    }
}