package database;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseInitializer {

    public static void initialize() {
        String sqlUsers = """
            CREATE TABLE IF NOT EXISTS tbl_users (
                user_id INTEGER PRIMARY KEY AUTOINCREMENT,
                username TEXT NOT NULL UNIQUE,
                password TEXT NOT NULL,
                roles TEXT NOT NULL,
                status TEXT NOT NULL
            );
        """;

        String sqlComponents = """
            CREATE TABLE IF NOT EXISTS tbl_components (
                component_id INTEGER PRIMARY KEY AUTOINCREMENT,
                category TEXT NOT NULL,
                brand TEXT NOT NULL,
                model_name TEXT NOT NULL,
                unit_price REAL NOT NULL,
                stock_quantity INTEGER NOT NULL,
                is_active INTEGER NOT NULL DEFAULT 1
            );
        """;

        String sqlBuildOrders = """
            CREATE TABLE IF NOT EXISTS tbl_build_orders (
                order_id INTEGER PRIMARY KEY AUTOINCREMENT,
                client_name TEXT NOT NULL,
                contact_number TEXT NOT NULL,
                order_date TEXT NOT NULL,
                total_price REAL NOT NULL,
                build_status TEXT NOT NULL,
                agent_id INTEGER,
                FOREIGN KEY (agent_id) REFERENCES tbl_users(user_id)
            );
        """;

        String sqlAssemblyRecords = """
            CREATE TABLE IF NOT EXISTS tbl_assembly_records (
                record_id INTEGER PRIMARY KEY AUTOINCREMENT,
                order_id INTEGER,
                component_id INTEGER,
                installed_serial_number TEXT,
                test_status TEXT,
                technician_id INTEGER,
                assembly_date TEXT,
                FOREIGN KEY (order_id) REFERENCES tbl_build_orders(order_id),
                FOREIGN KEY (component_id) REFERENCES tbl_components(component_id),
                FOREIGN KEY (technician_id) REFERENCES tbl_users(user_id)
            );
        """;

        String sqlAuditLogs = """
            CREATE TABLE IF NOT EXISTS tbl_audit_logs (
                log_id INTEGER PRIMARY KEY AUTOINCREMENT,
                user_id INTEGER,
                action_performed TEXT NOT NULL,
                timestamp TEXT NOT NULL,
                details TEXT,
                FOREIGN KEY (user_id) REFERENCES tbl_users(user_id)
            );
        """;

        // Added for Deliverable 8 Activity Logging System
        String sqlActivityLogs = """
            CREATE TABLE IF NOT EXISTS activity_logs (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                user_id INTEGER NOT NULL,
                action_type TEXT NOT NULL,
                description TEXT NOT NULL,
                timestamp DATETIME DEFAULT CURRENT_TIMESTAMP,
                FOREIGN KEY (user_id) REFERENCES tbl_users(user_id) ON DELETE CASCADE
            );
        """;

        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement()) {
            stmt.execute(sqlUsers);
            stmt.execute(sqlComponents);
            stmt.execute(sqlBuildOrders);
            stmt.execute(sqlAssemblyRecords);
            stmt.execute(sqlAuditLogs);
            stmt.execute(sqlActivityLogs); // Execute activity_logs creation
            System.out.println("[DatabaseInitializer] All RigBuild tables initialized successfully.");
        } catch (SQLException e) {
            System.err.println("Database initialization failed: " + e.getMessage());
        }
    }
}