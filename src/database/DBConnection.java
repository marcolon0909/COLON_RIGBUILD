package database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnection {
    private static final String URL = "jdbc:sqlite:RIGBUILD.db";

    public static Connection getConnection() {
        Connection conn = null;
        try {
            conn = DriverManager.getConnection(URL);
        } catch (SQLException e) {
            System.out.println("Connection to SQLite has failed.");
            e.printStackTrace();
        }
        return conn;
    }

    public static void testConnection() {
        try (Connection conn = getConnection()) {
            if (conn != null && !conn.isClosed()) {
                System.out.println("SUCCESS: Connected to SQLite database successfully!");
            } else {
                System.out.println("FAILURE: Connection is null or closed.");
            }
        } catch (SQLException e) {
            System.out.println("FAILURE: An error occurred while testing the connection.");
            e.printStackTrace();
        }
    }
}