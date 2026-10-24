package ui;

import dao.UserDao;
import model.User;
import java.util.List;
import java.util.Scanner;

public class AdminMenu {
    private final UserDao userDao;
    private final Scanner scanner;

    public AdminMenu(UserDao userDao, Scanner scanner) {
        this.userDao = userDao;
        this.scanner = scanner;
    }

    public void displayMenu() {
        while (true) {
            System.out.println("\n=== ADMIN DASHBOARD ===");
            System.out.println("1. View All System Users");
            System.out.println("2. View Pending Registrations");
            System.out.println("3. Approve User Account");
            System.out.println("4. Logout");
            System.out.print("Choose an option: ");

            String choice = scanner.nextLine();
            switch (choice) {
                case "1" -> viewAllUsers();
                case "2" -> viewPendingRegistrations();
                case "3" -> approveUserAccount();
                case "4" -> {
                    System.out.println("Logging out from Admin Dashboard.");
                    return;
                }
                default -> System.out.println("Invalid choice! Try again.");
            }
        }
    }

    private void printUserTable(List<User> users) {
        System.out.println("+-----+-----------------+---------------+------------+");
        System.out.printf("| %-3s | %-15s | %-13s | %-10s |%n", "ID", "Username", "Role", "Status");
        System.out.println("+-----+-----------------+---------------+------------+");
        for (User u : users) {
            System.out.printf("| %-3d | %-15s | %-13s | %-10s |%n",
                u.userId(), u.username(), u.roles(), u.status());
        }
        System.out.println("+-----+-----------------+---------------+------------+");
    }

    private void viewAllUsers() {
        System.out.println("\n--- ALL SYSTEM USERS ---");
        List<User> users = userDao.getAllUsers();
        if (users.isEmpty()) {
            System.out.println("No users found in the system.");
            return;
        }
        printUserTable(users);
    }

    private void viewPendingRegistrations() {
        System.out.println("\n--- PENDING REGISTRATIONS ---");
        List<User> pendingUsers = userDao.getPendingUsers();
        if (pendingUsers.isEmpty()) {
            System.out.println("No pending registrations found.");
            return;
        }
        printUserTable(pendingUsers);
    }

    private void approveUserAccount() {
        System.out.println("\n--- APPROVE USER ACCOUNT ---");
        List<User> pendingUsers = userDao.getPendingUsers();
        if (pendingUsers.isEmpty()) {
            System.out.println("No pending accounts available for approval.");
            return;
        }
        printUserTable(pendingUsers);

        System.out.print("Enter User ID to approve (Enter 0 to cancel): ");
        String input = scanner.nextLine();

        int targetId;
        try {
            targetId = Integer.parseInt(input);
        } catch (NumberFormatException e) {
            System.out.println("Invalid input. Action cancelled.");
            return;
        }

        if (targetId == 0) {
            System.out.println("Approval action cancelled.");
            return;
        }

        boolean exists = pendingUsers.stream().anyMatch(u -> u.userId() == targetId);
        if (!exists) {
            System.out.println("Error: User ID not found in the pending list.");
            return;
        }

        boolean success = userDao.approveUser(targetId);
        if (success) {
            System.out.println("Success! User ID " + targetId + " status updated to 'APPROVED'.");
        } else {
            System.out.println("Failed to update user status.");
        }
    }
}