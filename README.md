# Banking System

A console banking application written in Java. Users can open savings and checking
accounts, deposit, withdraw and transfer money, apply interest to savings accounts,
and view reports. All data is kept in memory and is lost when the application exits.

This project is a practice exercise covering object-oriented programming, collections
and streams, exceptions, Maven, configuration files, unit testing with JUnit and a Git
pull request workflow.

## Features

| Menu option | Description |
| --- | --- |
| 1. Open savings account | Requires a minimum initial deposit; earns interest |
| 2. Open checking account | Allows an overdraft down to a configured limit |
| 3. Deposit | Adds money to any account |
| 4. Withdraw | Removes money, respecting each account type's limits |
| 5. Transfer | Moves money between two accounts; nothing changes if it fails |
| 6. Account details | Shows an account and its full transaction history |
| 7. List all accounts | Shows all accounts sorted by account number |
| 8. Apply interest | Applies interest to every savings account |
| 9. Reports | Accounts below a balance, total balance, transactions by type, owner names, total deposited, accounts by type |

Each owner may hold a limited number of accounts (see Configuration).

## Requirements

- JDK 25 or newer
- Apache Maven 3.9 or newer, available on the PATH

## How to build and test

From the project root:

    mvn clean package

This compiles the code and runs all unit tests. The build is successful when Maven
prints `BUILD SUCCESS`.

To run only the tests:

    mvn test

The summary line shows the number of tests run and any failures.

## How to run

Build first, then:

    java -cp target/classes io.github.sabo2560.bank.App

Alternatively, open the project in IntelliJ IDEA and run the `App` class.

## Configuration

Settings are read from `src/main/resources/config.properties`:

| Key | Default | Purpose |
| --- | --- | --- |
| `bank.name` | `Sabo Bank` | Name shown in the menu header |
| `savings.interest.rate` | `0.03` | Interest rate applied to savings accounts (0.03 = 3%) |
| `savings.minimum.balance` | `100.0` | Minimum balance a savings account must keep |
| `checking.overdraft.limit` | `500.0` | How far below zero a checking account may go |
| `max.accounts.per.owner` | `3` | Maximum number of accounts, of any type, one owner may hold |

If the file or a key is missing, or a value is not a number, the application prints
a configuration error naming the problem and exits with code 1.

The unit tests read the same file, so changing these values can make tests fail.

## Project structure

    src/main/java/io/github/sabo2560/bank/
      App.java                  console menu, the only class that reads input or prints
      config/
        ConfigReader.java       loads config.properties once, static access
      model/
        Account.java            abstract base class, owns all balance changes
        SavingsAccount.java     minimum balance, implements InterestBearing
        CheckingAccount.java    overdraft limit
        InterestBearing.java    interface for accounts that earn interest
        Transaction.java        immutable record of one balance change
        TransactionType.java    enum of transaction kinds
      service/
        Bank.java               stores accounts in a HashMap, business operations, reports
      exception/
        InsufficientFundsException.java
        AccountNotFoundException.java
        TooManyAccountsException.java
        ConfigException.java
    src/main/resources/
      config.properties
    src/test/java/io/github/sabo2560/bank/
      model/
        SavingsAccountTest.java
        CheckingAccountTest.java
      service/
        BankTest.java

Design rules:

- Only `App` talks to the user. `model` and `service` classes never print or read input.
- An account's balance changes in one place only: `Account.addTransaction`.
- Each account type defines its own withdrawal limit through `getAvailableToWithdraw()`;
  `Account` applies the same withdraw and transfer logic to all types.
- Business rules, such as the account limit per owner, are enforced in `Bank`, so they
  apply to every caller.

## Exceptions

| Exception | Type | Thrown when |
| --- | --- | --- |
| `InsufficientFundsException` | Checked | A withdrawal or transfer exceeds the available amount |
| `AccountNotFoundException` | Unchecked | An account number does not exist |
| `TooManyAccountsException` | Unchecked | An owner already holds the maximum number of accounts |
| `ConfigException` | Unchecked | The config file or a key is missing, or a value is invalid |

Invalid amounts (zero or negative), blank owner names and non-numeric input use the
built-in `IllegalArgumentException` (and its subclass `NumberFormatException`).

### Why checked vs unchecked

`InsufficientFundsException` is checked because running out of money is a normal
business situation. It can happen even when all code is correct, and the caller
can react to it, for example by showing a message and letting the user try a smaller
amount. Making it checked forces every caller to handle it, so it cannot be forgotten.

`AccountNotFoundException` is unchecked because an unknown account number comes from
invalid input, not from a business rule. Forcing try/catch around every account lookup
would add noise without helping. The console layer still catches it to show a
friendly message.

`TooManyAccountsException` is unchecked because the limit is enforced in one place,
`Bank`. Making it checked would force `throws` onto every method that opens accounts,
including test setup, without much benefit. The console layer still catches it.

`ConfigException` is unchecked because a broken configuration is a setup error the
program cannot recover from; the application stops at startup with a clear message.

## Testing

Scenarios 1 to 13 are automated with JUnit and run with `mvn test`, together with
tests for the account limit and the accounts-by-type report. Scenarios 14 and 15 cover
console behavior and are checked manually.

| # | Scenario | Expected result |
| --- | --- | --- |
| 1 | Open savings with 1000 | Account created, one `DEPOSIT` transaction |
| 2 | Open savings with 50 | Rejected: below minimum balance |
| 3 | Open account with blank owner | Rejected: owner name required |
| 4 | Deposit 0 or a negative amount | Rejected, balance unchanged |
| 5 | Savings 1000: withdraw 901, then 900 | First rejected (available 900.00), second leaves 100.00 |
| 6 | Checking 200: withdraw 700 | Balance -500.00 |
| 7 | Checking at -500: withdraw 0.01 | Rejected, available 0.00 |
| 8 | Savings 1000: apply interest | Balance 1030.00, one `INTEREST` transaction |
| 9 | Look up `ACC-9999` | Account not found |
| 10 | Transfer 300 from savings to checking | Both balances and transactions updated |
| 11 | Transfer more than available | Rejected, no balance changes |
| 12 | Transfer to `ACC-9999` | Account not found, sender unchanged |
| 13 | Reports | Correct filtering, totals, distinct sorted owner names |
| 14 | Non-numeric input | Error message, menu shown again |
| 15 | Missing config key | Configuration error, exit code 1 |

## Development workflow

Each feature was developed on its own branch (`feature/BANK-<n>-<name>`) and merged
into `main` through a pull request. Maintenance changes use `chore/<name>` branches.

## Decisions

Choices made where the specification was not specific:

- **Java 25 target**: the code compiles for Java 25 (the current long-term support
  release) instead of 27, so that IntelliJ IDEA 2025.2 recognizes the language level.
  It builds and runs on JDK 25 or newer.
- **Money type**: all amounts use `BigDecimal` instead of `double`, so decimal
  arithmetic is exact (with `double`, 0.1 + 0.2 is 0.30000000000000004). Amounts are
  created from strings, and interest is rounded to 2 decimals with `RoundingMode.HALF_UP`.
- **Account numbers after a rejected savings account**: the account number is assigned
  in the `Account` constructor before `SavingsAccount` checks the minimum deposit, so a
  rejected savings account still uses up a number. Numbers stay unique; gaps are possible.
- **Rejected owner names**: blank names are rejected before a number is assigned, so
  they do not create gaps.
- **Account limit and owner names**: owners are matched with `equals`, so the check is
  case-sensitive: `Sabo` and `sabo` count as different owners.
- **Number formatting**: all amounts are formatted with `Locale.US` so decimals always
  use a dot, regardless of the system's regional settings.
- **Transaction types in reports**: the type is entered as text and is not case-sensitive.
- **Test framework**: JUnit 6 is used instead of JUnit 5 (the successor, same API).