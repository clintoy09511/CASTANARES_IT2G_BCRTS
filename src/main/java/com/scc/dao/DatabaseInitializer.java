package com.scc.dao;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseInitializer {

    public static void initialize() {
        String createResidentsTable = """
                CREATE TABLE IF NOT EXISTS Residents (
                    resident_id INTEGER PRIMARY KEY AUTOINCREMENT,
                    first_name TEXT NOT NULL,
                    last_name TEXT NOT NULL,
                    middle_name TEXT,
                    birth_date TEXT,
                    gender TEXT,
                    address TEXT,
                    contact_number TEXT,
                    civil_status TEXT,
                    status TEXT DEFAULT 'Active'
                );
                """;

        String createUsersTable = """
                CREATE TABLE IF NOT EXISTS Users (
                    user_id INTEGER PRIMARY KEY AUTOINCREMENT,
                    username TEXT NOT NULL UNIQUE,
                    password TEXT NOT NULL,
                    full_name TEXT NOT NULL,
                    role TEXT NOT NULL,
                    status TEXT DEFAULT 'Active'
                );
                """;

        String createClearanceRequestsTable = """
                CREATE TABLE IF NOT EXISTS Clearance_Requests (
                    clearance_id INTEGER PRIMARY KEY AUTOINCREMENT,
                    resident_id INTEGER NOT NULL,
                    request_date TEXT DEFAULT CURRENT_TIMESTAMP,
                    purpose TEXT NOT NULL,
                    status TEXT DEFAULT 'Pending',
                    processed_by INTEGER,
                    FOREIGN KEY (resident_id) REFERENCES Residents(resident_id),
                    FOREIGN KEY (processed_by) REFERENCES Users(user_id)
                );
                """;

        String createPaymentsTable = """
                CREATE TABLE IF NOT EXISTS Payments (
                    payment_id INTEGER PRIMARY KEY AUTOINCREMENT,
                    clearance_id INTEGER NOT NULL,
                    amount REAL NOT NULL,
                    payment_date TEXT DEFAULT CURRENT_TIMESTAMP,
                    payment_status TEXT DEFAULT 'Unpaid',
                    recorded_by INTEGER,
                    FOREIGN KEY (clearance_id) REFERENCES Clearance_Requests(clearance_id),
                    FOREIGN KEY (recorded_by) REFERENCES Users(user_id)
                );
                """;

        String createActivityLogsTable = """
                CREATE TABLE IF NOT EXISTS Activity_Logs (
                    log_id INTEGER PRIMARY KEY AUTOINCREMENT,
                    user_id INTEGER,
                    action TEXT NOT NULL,
                    description TEXT,
                    timestamp TEXT DEFAULT CURRENT_TIMESTAMP,
                    FOREIGN KEY (user_id) REFERENCES Users(user_id)
                );
                """;

        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement()) {

            // Enable foreign key enforcement in SQLite
            stmt.execute("PRAGMA foreign_keys = ON;");

            stmt.execute(createResidentsTable);
            stmt.execute(createUsersTable);
            stmt.execute(createClearanceRequestsTable);
            stmt.execute(createPaymentsTable);
            stmt.execute(createActivityLogsTable);

            System.out.println("SQLite database (VIRMS.db) initialized successfully.");
        } catch (SQLException e) {
            System.err.println("Error initializing VIRMS.db: " + e.getMessage());
            e.printStackTrace();
        }
    }
}