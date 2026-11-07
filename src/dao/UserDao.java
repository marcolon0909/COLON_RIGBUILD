package dao;

import database.DBConnection;
import model.User;
import java.sql.*;

public class UserDao {

    /**
     * Checks if a user ID exists in the database.
     */
    public boolean userExists(int userId) {
        String sql = "SELECT 1 FROM tbl_users WHERE user_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, userId);
            ResultSet rs = pstmt.executeQuery();
            return rs.next();
        } catch (SQLException e) {
            return false;
        }
    }

    /**
     * Deliverable 7: Updates a user's role and status using a parameterized PreparedStatement.
     */
    public boolean updateUserRoleAndStatus(int userId, String newRole, String newStatus) {
        String sql = "UPDATE tbl_users SET roles = ?, status = ? WHERE user_id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, newRole.trim().toUpperCase());
            pstmt.setString(2, newStatus.trim().toUpperCase());
            pstmt.setInt(3, userId);

            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;

        } catch (SQLException e) {
            System.out.println("❌ Database Error updating user: " + e.getMessage());
            return false;
        }
    }

    /**
     * Authenticates a user login and returns the User record.
     */
    public User authenticate(String username, String password) {
        String sql = "SELECT * FROM tbl_users WHERE username = ? AND password = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, username);
            pstmt.setString(2, password);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return new User(
                    rs.getInt("user_id"),
                    rs.getString("username"),
                    rs.getString("password"),
                    rs.getString("roles"),
                    rs.getString("status")
                );
            }
        } catch (SQLException e) {
            System.out.println("❌ Login Error: " + e.getMessage());
        }
        return null;
    }

    /**
     * Registers a new user.
     */
    public boolean registerUser(String username, String password, String role) {
        String sql = "INSERT INTO tbl_users (username, password, roles, status) VALUES (?, ?, ?, 'PENDING')";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, username);
            pstmt.setString(2, password);
            pstmt.setString(3, role.toUpperCase());
            pstmt.executeUpdate();
            return true;
        } catch (SQLException e) {
            System.out.println("❌ Registration Error: " + e.getMessage());
            return false;
        }
    }

    /**
     * Displays all users.
     */
    public void displayAllUsers() {
        String sql = "SELECT user_id, username, roles, status FROM tbl_users";
        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            System.out.println("\n--------------------------------------------------");
            System.out.printf("%-8s | %-15s | %-12s | %-10s\n", "User ID", "Username", "Role", "Status");
            System.out.println("--------------------------------------------------");

            while (rs.next()) {
                int id = rs.getInt("user_id");
                String username = rs.getString("username");
                String role = rs.getString("roles");
                String status = rs.getString("status");

                System.out.printf("%-8d | %-15s | %-12s | %-10s\n", id, username, role, status);
            }
            System.out.println("--------------------------------------------------\n");

        } catch (SQLException e) {
            System.out.println("❌ Database Error displaying users: " + e.getMessage());
        }
    }
}