# 💰 Expense and Income Tracker

A sleek desktop Java Swing application designed to track income, manage daily expenses, and calculate net balances in real time with MySQL database persistence.

---

## ✨ Features

- **Dashboard Summary Cards**:
  - **Expense**: Real-time total expenses formatted in Indian Rupees (₹).
  - **Income**: Real-time total income received.
  - **Total Balance**: Dynamic net balance calculation (`Income - Expenses`) with negative indicator when in deficit.
- **Transaction Management**:
  - Add new transactions (Income or Expense) with descriptions and amounts.
  - Remove existing transactions with instant dynamic re-calculation.
  - Input validation to ensure clean numerical data entry.
- **Custom Modern Swing UI**:
  - Undecorated window with custom draggable title bar and window controls.
  - Gradient table headers and custom-styled scrollbars.
  - Color-coded rows (Green for Income, Orange/Red for Expense).
  - Dynamic `#` row numbering decoupled from database primary keys.
- **Database Persistence**:
  - Built using JDBC with MySQL database integration.
  - Uses the Data Access Object (DAO) design pattern for clean separation of concerns.

---

## 🛠️ Tech Stack

- **Language**: Java (JDK 17+ or JDK 21)
- **GUI Framework**: Java Swing & AWT
- **Database**: MySQL (via XAMPP / WAMP / Standalone MySQL)
- **Connectivity**: JDBC (`mysql-connector-j-8.x.x.jar`)
- **Architecture**: MVC / DAO Pattern

---

## 📂 Project Structure

```text
Java Project - Expense Tracker/
├── lib/
│   └── mysql-connector-j-8.3.0.jar      # MySQL JDBC Driver
├── src/
│   ├── DatabaseConnection.java          # JDBC Connection Manager
│   ├── TrackerApp.java                  # Main UI & Application Logic
│   ├── Transaction.java                 # Transaction Model Entity
│   ├── TransactionDAO.java              # Database Access Object
│   └── TransactionValuesCalculation.java # Utility for Financial Calculations
├── schema.sql                           # MySQL Database Schema Setup
├── .gitignore
└── README.md
```

---

## 🚀 Getting Started

### 1. Prerequisites
- **Java Development Kit (JDK)**: Java 17 or higher.
- **MySQL Server**: Installed via [XAMPP](https://www.apachefriends.org/), WAMP, or standalone MySQL.
- **IDE**: [VS Code](https://code.visualstudio.com/) or [IntelliJ IDEA](https://www.jetbrains.com/idea/).

---

### 2. Database Setup

1. Start **Apache** and **MySQL** in your **XAMPP Control Panel**.
2. Open **phpMyAdmin** (`http://localhost/phpmyadmin`) or your MySQL terminal.
3. Import the [`schema.sql`](schema.sql) file or run the following SQL query:

```sql
CREATE DATABASE IF NOT EXISTS `expense_income_db`;
USE `expense_income_db`;

CREATE TABLE IF NOT EXISTS `transaction_table` (
    `id` INT AUTO_INCREMENT PRIMARY KEY,
    `transaction_type` VARCHAR(50) NOT NULL,
    `description` VARCHAR(255) NOT NULL,
    `amount` DOUBLE NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
```

4. If your MySQL server has a root password, configure it in [`src/DatabaseConnection.java`](src/DatabaseConnection.java):
```java
private static final String USER = "root";
private static final String PASSWORD = ""; // Update if password is set
```

---

### 3. How to Run

#### Option A: Running in VS Code
1. Open the project folder in VS Code.
2. Install the **Extension Pack for Java** (if not already installed).
3. Open `src/TrackerApp.java` and click **Run** above the `main` method (or press **`F5`**).

#### Option B: Running in IntelliJ IDEA
1. Open the project in IntelliJ IDEA.
2. Ensure the MySQL Connector JAR in `lib/` is added as a module dependency (**File** $\rightarrow$ **Project Structure** $\rightarrow$ **Modules** $\rightarrow$ **Dependencies**).
3. Open `src/TrackerApp.java` and click the **green Play button ▶** next to the `main` method.

#### Option C: Running from Command Line (Terminal / PowerShell)
```powershell
# Compile
javac -cp "lib/*;src" -d out src/*.java

# Run
java -cp "out;lib/*" TrackerApp
```

---

## 📝 License

This project is open-source and available under the [MIT License](LICENSE).
