# Stock Analysis Interface

## Overview

This project delivers a streamlined, text-based user interface built with **Java** and **MySQL**, designed to facilitate stock analysis without the complexity of modern UI frameworks. It demonstrates how foundational tools can form an effective, intuitive experience for financial data exploration.

## Objective

The goal is to construct a robust, simple-to-use Java application that pulls stock data via SQL queries, allowing users to perform analysis on demand. This serves as both an educational exercise in database-driven application design and a practical tool for understanding the link between Java and SQL.

## Key Features

- **Text-Based Interface**: Lightweight and accessible, users navigate through command-line prompts to request data and run analysis.
- **Java–SQL Integration**: Utilizes JDBC to communicate with MySQL, enabling real-time data retrieval and manipulation.
- **Core Stock Analytics**:
  - Query stock price history
  - Perform basic metrics (e.g., averages, highs/lows)
  - Filter by date, ticker, or other attributes
- **Portable Code**: Console-based, easy to deploy without requiring GUI libraries or complex dependencies.

## Architecture

1. **Database Layer**:
   - MySQL schema designed to store historical stock data.
   - SQL queries for filtering, aggregation, and retrieval.

2. **Java Backend**:
   - JDBC-based connection to execute queries and handle result sets.
   - Data models to encapsulate stock information.

3. **User Interface**:
   - Simple menu-driven prompts guide users through querying, analyzing, and viewing data.
   - Results are formatted for clarity and readability in the terminal.


## Requirements

- Java JDK (version 8 or newer)
- MySQL Server (and appropriate JDBC driver)
- Optional: Python for supplementary tooling in `PythonDBAPI/`

## Getting Started

1. **Database Setup**: Load the sample schema and stock data into your MySQL instance.
2. **Compile Java**:
   ```bash
   javac -cp .:path/to/mysql-connector-java.jar PortfolioTracker.java
   ```
3. **Run the application**:
   ```bash
   java -cp .:path/to/mysql-connector-java.jar PortfolioTracker
   ```
4. **Follow prompts** to query price data, compute statistics, and navigate through your analysis.

---
