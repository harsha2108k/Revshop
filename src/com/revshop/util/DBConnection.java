package com.revshop.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnection {
    // Database credentials - typically should be effectively loaded from a config file
    // For this console app, we keep them here as requested.
    private static final String URL = "jdbc:oracle:thin:@localhost:1521:xe"; // Adjust SID/Service as needed
    private static final String USERNAME = "revshop"; // PLACEHOLDER
    private static final String PASSWORD = "revshop"; // PLACEHOLDER

    private static Connection connection = null;

    // Private constructor for Singleton
    private DBConnection() {}

    public static Connection getConnection() {
        if (connection == null) {
            try {
                // Load Oracle JDBC Driver
                Class.forName("oracle.jdbc.driver.OracleDriver");
                connection = DriverManager.getConnection(URL, USERNAME, PASSWORD);
//                System.out.println("Connected to the database successfully.");
            } catch (ClassNotFoundException e) {
                System.err.println("Oracle JDBC Driver not found. Add ojdbc jar to classpath.");
//                e.printStackTrace();
            } catch (SQLException e) {
                System.err.println("Connection Failed! Check output console"+ e.getMessage());
//                e.printStackTrace();
            }
        } else {
            // Optional: Check if closed and reconnect
            try {
                if (connection.isClosed()) {
                     connection = DriverManager.getConnection(URL, USERNAME, PASSWORD);
                }
            } catch (SQLException e) {
            	System.err.println("Connection Failed! Check output console"+ e.getMessage());
//                e.printStackTrace();
            }
        }
        return connection;
    }
}
