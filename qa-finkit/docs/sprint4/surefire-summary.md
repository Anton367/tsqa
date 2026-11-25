# Звіт за результатами виконання тестів Maven Surefire (Спринт 4)

## 1. Загальні відомості про середовища та запуск
* **Модуль:** `finkit-module` (`v1`)
* **Джерело сирих звітів:** `target/surefire-reports/` (`TEST-*.xml`, `*.txt`)
* **Команди запуску:**
  * Модульні тести (без UI): `mvn test`
  * Усі тести (включно з UI-тестами `@Tag("ui")`): `mvn test -DexcludedGroups=none`
* **Тестові середовища учасників:**
  * **Учасник Г (Даша):** Windows, JDK 17, Apache Maven 3.10.2, Firefox (Selenium WebDriver)
  * **Учасник ТА (Антон):** Linux (`7.3.4-1-cachyos`), OpenJDK 27 (`27.0.1.0.3`), Firefox (Selenium WebDriver)

---

## 2. Результати тестування — Учасник Г (Даша)

### 2.1. Модульне тестування (`mvn test`)
| Показник | Значення | Примітка |
| :--- | :---: | :--- |
| **Запущено тестів (Tests run)** | **12** | Базовий набір тестів модуля `v1` |
| **Пройдено успішно (Passed)** | **10** | Коректна робота перевірених сценаріїв |
| **Провалено (Failures)** | **1** | Дефект бізнес-логіки у `AccountLedger` |
| **Помилки виконання (Errors)** | **1** | Неочікуваний виняток у `AccountLedger` |
| **Пропущено (Skipped)** | **0** | UI-тести виключені за замовчуванням (`@Tag("ui")`) |

### 2.2. Деталізація падінь у модулі `v1` (`AccountLedgerTest`)
1. **`AccountLedgerTest.historyPreservesExecutionOrderAndBalances` — Failure**
   * **Суть:** порушення очікуваного порядку записів або розрахунку проміжних балансів в історії операцій рахунку.
   * **Статус:** дефект модуля `v1`, підлягає реєстрації в GitHub Issues з міткою `module`.
2. **`AccountLedgerTest.withdrawFullBalanceLeavesZero` — Error**
   * **Суть:** під час спроби зняття повної суми доступного балансу виникає помилка виконання замість коректного обнулення залишку.
   * **Статус:** дефект модуля `v1`, підлягає реєстрації в GitHub Issues з міткою `module`.

### 2.3. UI-тести Selenium WebDriver (`mvn test -DexcludedGroups=none`)
* **Класи тестів (`ua.kpi.tzyapz.finkit.ui`):** `LoginTest`, `AccountsTest`, `OpenAccountFormTest`
* **Цільовий стенд:** ParaBank (`https://parabank.parasoft.com/parabank/index.htm`)
* **Статус:** **Passed** (усі 3 UI-тести успішно виконані).

---

## 3. Результати тестування — Учасник ТА (Антон)

### 3.1. Зведена таблиця за звітами `surefire-reports.zip` (`mvn test -DexcludedGroups=none`)
| Набір тестів (Test suite) | Тип | Tests run | Passed | Failures | Errors | Skipped | Час (с) |
| :--- | :---: | :---: | :---: | :---: | :---: | :---: | :---: |
| `ua.kpi.tzyapz.finkit.ExampleTest` | Unit | 2 | 2 | 0 | 0 | 0 | 0.093 |
| `ua.kpi.tzyapz.finkit.TransferValidatorTest` | Unit | 6 | 4 | 2 | 0 | 0 | 0.139 |
| `ua.kpi.tzyapz.finkit.ui.ForgotInfoTest` | UI (`@Tag("ui")`) | 1 | 1 | 0 | 0 | 0 | 7.694 |
| `ua.kpi.tzyapz.finkit.ui.NavigationTest` | UI (`@Tag("ui")`) | 1 | 1 | 0 | 0 | 0 | 8.351 |
| **Разом по запуску Антона** | **Unit + UI** | **10** | **8** | **2** | **0** | **0** | **16.277** |

### 3.2. Деталізація падінь у `TransferValidatorTest`
Під час запуску `TransferValidatorTest` (6 тестових викликів включно з `@ParameterizedTest`) зафіксовано **2 падіння (Failures)**:
1. **`TransferValidatorTest.isValidIban_WrongMod97_ReturnsFalse` — Failure**
   * **Деталі:** `org.opentest4j.AssertionFailedError: expected: <false> but was: <true>` (перевірявся IBAN `"UA471111111234567891234567892"`).
   * **Примітка:** контрольна сума MOD-97 у модулі `v1` або у тестовому значенні потребує звірки (`TransferValidator.isValidIban` повернув `true`).
2. **`TransferValidatorTest.validate_InvalidToIban_ReturnsInvalidToIban` — Failure**
   * **Деталі:** `org.opentest4j.AssertionFailedError: expected: <[INVALID_TO_IBAN]> but was: <[]>`.
   * **Примітка:** при передачі невалідного `toIban` (`"INVALID_IBAN"`) метод `validator.validate(...)` у модулі `v1` повернув порожній список помилок `[]` замість очікуваного `INVALID_TO_IBAN` (потенційний дефект валідації отримувача в модулі `v1`).

---

## 4. Загальне зведення по проєкту (Учасник Г + Учасник ТА)
* **Усього виконано тестів (унікальні прогони):** **22** (12 Даша + 10 Антон; з них 2 тести `ExampleTest` спільні у базовому наборі).
* **UI-тести (усі 5 сценаріїв):** **5/5 Passed** (`LoginTest`, `AccountsTest`, `OpenAccountFormTest`, `ForgotInfoTest`, `NavigationTest`).
* **Виявлені дефекти та розбіжності в модулі `v1`:**
  * `AccountLedger` — 2 падіння (`historyPreservesExecutionOrderAndBalances`, `withdrawFullBalanceLeavesZero`).
  * `TransferValidator` — 2 падіння (`validate_InvalidToIban_ReturnsInvalidToIban`, `isValidIban_WrongMod97_ReturnsFalse`).

---

## 5. Артефакти Selenium IDE (Задача #17)
Записано, доповнено командами очікування (`wait for element visible`) та перевірки (`assert element present`), успішно відтворено у Firefox та експортовано:
* **Файл проєкту Selenium IDE:** `selenium-ide/finkit.side` (сценарій `TC-SRS-001 Login and Accounts Overview`)
* **Експортований JUnit-код:** `selenium-ide/export/FinkitldeExport.java`

---

## 6. Склад фінального `.zip`-пакета для документації (`README.md`)
* Підсумковий звіт: `docs/sprint4/Surefire-summary.md`
* Сирі звіти Surefire (Даша + Антон): `target/surefire-reports/*.xml`, `target/surefire-reports/*.txt`
* Артефакти Selenium IDE: `selenium-ide/finkit.side`, `selenium-ide/export/FinkitldeExport.java`
* UI-тести: `src/test/java/ua/kpi/tzyapz/finkit/ui/*.java`