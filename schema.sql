-- =======================================================
-- Expense and Income Tracker Database Schema
-- =======================================================

-- Create Database
CREATE DATABASE IF NOT EXISTS `expense_income_db`;
USE `expense_income_db`;

-- Create Transactions Table
CREATE TABLE IF NOT EXISTS `transaction_table` (
    `id` INT AUTO_INCREMENT PRIMARY KEY,
    `transaction_type` VARCHAR(50) NOT NULL,
    `description` VARCHAR(255) NOT NULL,
    `amount` DOUBLE NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

