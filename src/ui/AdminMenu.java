package ui;

import dao.ActivityLogDao;
import dao.UserDao;
import model.ActivityLog;
import java.util.List;
import java.util.Scanner;

public class AdminMenu {
    private final Scanner scanner;
    private final UserDao userDao;
    private final ActivityLogDao activityLogDao;
    private final int currentAdminId;

    public AdminMenu() {
        this.scanner = new Scanner(System.in);
        this.userDao = new UserDao();
        this.activityLogDao = new ActivityLogDao();
        this.currentAdminId = 1;
    }

    public AdminMenu(int currentAdminId) {
        this.scanner = new Scanner(System.in);
        this.userDao = new UserDao();
        this.activityLogDao = new ActivityLogDao();
        this.currentAdminId = currentAdminId;
    }

    public AdminMenu(UserDao userDao, Scanner scanner) {
        this.userDao = userDao;
        this.scanner = scanner;
        this.activityLogDao = new ActivityLogDao();
        this.currentAdminId = 1;
    }

    public AdminMenu(UserDao userDao, Scanner scanner, int currentAdminId) {
        this.userDao = userDao;
        this.scanner = scanner;
        this.activityLogDao = new ActivityLogDao();
        this.currentAdminId = currentAdminId;
    }

    public void displayMenu() {
        showMenu(this.currentAdminId);
    }

    public void showMenu(int adminId) {
        int choice = -1;
        do {
            System.out.println("\n--- ADMIN MENU ---");
            System.out.println("1. View All Users");
            System.out.println("2. Update User Role & Status");
            System.out.println("3. View System Activity Logs");
            System.out.println("0. Logout / Exit");

            choice = readIntInput("Enter choice: ");

            switch (choice) {
                case 1:
                    userDao.displayAllUsers();
                    break;
                case 2:
                    handleUpdateUser(adminId);
                    break;
                case 3:
                    handleViewActivityLogs();
                    break;
                case 0:
                    System.out.println("Logging out from Admin menu...");
                    break;
                default:
                    System.out.println("❌ Invalid option. Please try again.");
            }
        } while (choice != 0);
    }

    public void handleUpdateUser(int currentAdminId) {
        System.out.println("\n--- UPDATE USER ROLE & STATUS ---");

        userDao.displayAllUsers();

        int targetUserId = readIntInput("Enter Target User ID to Update (or 0 to Cancel): ");
        if (targetUserId == 0) {
            System.out.println("Operation canceled.");
            return;
        }

        // Security Rule 1: Self-Edit Restriction
        if (targetUserId == currentAdminId) {
            System.out.println("WARNING: Modifying the currently active admin account is restricted.");
            return;
        }

        // Security Rule 2: Check if User ID actually exists in database
        if (!userDao.userExists(targetUserId)) {
            System.out.println("❌ User ID " + targetUserId + " does not exist in the database.");
            return;
        }

        String newRole = readValidRole();
        if (newRole.equals("0")) {
            System.out.println("Operation canceled.");
            return;
        }

        String newStatus = readValidStatus();
        if (newStatus.equals("0")) {
            System.out.println("Operation canceled.");
            return;
        }

        // Pass currentAdminId as the first parameter
        boolean success = userDao.updateUserRoleAndStatus(currentAdminId, targetUserId, newRole, newStatus);
        if (success) {
            System.out.println("✅ User ID " + targetUserId + " successfully updated to Role: " + newRole + ", Status: " + newStatus);
        } else {
            System.out.println("❌ Failed to update user.");
        }
    }

    private void handleViewActivityLogs() {
        System.out.println("\n--- SYSTEM ACTIVITY LOGS (AUDIT TRAIL) ---");
        List<ActivityLog> logs = activityLogDao.getAllLogs();

        if (logs.isEmpty()) {
            System.out.println("No activity logs recorded yet.");
            return;
        }

        System.out.println("---------------------------------------------------------------------------------------------------------");
        System.out.printf("%-5s | %-8s | %-15s | %-45s | %-20s\n", "ID", "User ID", "Action Type", "Description", "Timestamp");
        System.out.println("---------------------------------------------------------------------------------------------------------");

        for (ActivityLog log : logs) {
            System.out.printf("%-5d | %-8d | %-15s | %-45s | %-20s\n",
                    log.id(),
                    log.userId(),
                    log.actionType(),
                    log.description(),
                    log.timestamp()
            );
        }
        System.out.println("---------------------------------------------------------------------------------------------------------");
    }

    // --- Input Validation Wrappers ---

    private int readIntInput(String prompt) {
        while (true) {
            System.out.print(prompt);
            String input = scanner.nextLine().trim();
            try {
                return Integer.parseInt(input);
            } catch (NumberFormatException e) {
                System.out.println("❌ Invalid input! Please enter a valid number.");
            }
        }
    }

    private String readValidRole() {
        while (true) {
            System.out.print("Enter New Role (ADMIN / TECHNICIAN / SALES_AGENT / USER) [or 0 to Cancel]: ");
            String role = scanner.nextLine().trim().toUpperCase();

            if (role.equals("0")) return "0";
            if (role.equals("ADMIN") || role.equals("TECHNICIAN") || role.equals("SALES_AGENT") || role.equals("USER")) {
                return role;
            }
            System.out.println("❌ Invalid role! Allowed: ADMIN, TECHNICIAN, SALES_AGENT, USER.");
        }
    }

    private String readValidStatus() {
        while (true) {
            System.out.print("Enter New Status (APPROVED / PENDING) [or 0 to Cancel]: ");
            String status = scanner.nextLine().trim().toUpperCase();

            if (status.equals("0")) return "0";
            if (status.equals("APPROVED") || status.equals("PENDING")) {
                return status;
            }
            System.out.println("❌ Invalid status! Allowed: APPROVED, PENDING.");
        }
    }
}