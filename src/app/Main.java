package app;

import database.DBConnection;
import dao.UserDAO;
import model.User;

import java.util.List;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        // Automatically tests the database connection on startup
        System.out.println("=== INITIALIZING RIGBUILD ===");
        DBConnection.testConnection();

        Scanner scanner = new Scanner(System.in);
        UserDAO userDAO = new UserDAO();
        boolean running = true;

        while (running) {
            System.out.println("\n===== RIGBUILD SYSTEM =====");
            System.out.println("1. Log In");
            System.out.println("2. Register Account (Sign Up)");
            System.out.println("3. Exit Program");
            System.out.print("Select an option: ");

            while (!scanner.hasNextInt()) {
                System.out.println("Invalid input. Please enter a number.");
                scanner.next();
                System.out.print("Select an option: ");
            }
            int choice = scanner.nextInt();
            scanner.nextLine();

            switch (choice) {
                case 1:
                    handleLogin(scanner, userDAO);
                    break;
                case 2:
                    handleRegister(scanner, userDAO);
                    break;
                case 3:
                    System.out.println("\nExiting RigBuild. Goodbye!");
                    running = false;
                    break;
                default:
                    System.out.println("\nInvalid choice. Please select 1, 2, or 3.");
            }
        }
        scanner.close();
    }

    public static void handleLogin(Scanner scanner, UserDAO userDAO) {
        System.out.println("\n--- RIGBUILD LOGIN ---");
        System.out.print("Username: ");
        String username = scanner.nextLine();
        System.out.print("Password: ");
        String password = scanner.nextLine();

        User loggedInUser = userDAO.login(username, password);

        if (loggedInUser != null) {
            System.out.println("\nLogin Successful! Welcome, " + loggedInUser.getUsername());
            System.out.println("Role: " + loggedInUser.getRole());

            try { Thread.sleep(800); } catch (InterruptedException e) {}

            String role = loggedInUser.getRole().trim().toLowerCase();

            if (role.contains("admin")) {
                showAdminMenu(scanner, userDAO);
            } else if (role.contains("sales")) {
                showSalesMenu(scanner);
            } else if (role.contains("tech")) {
                showTechnicianMenu(scanner);
            } else {
                System.out.println("\nUnknown role permission. Access denied.");
                pressEnterToContinue(scanner);
            }
        } else {
            System.out.println("\nLogin failed. Check credentials or pending account status.");
            try { Thread.sleep(1000); } catch (InterruptedException e) {}
        }
    }

    public static void handleRegister(Scanner scanner, UserDAO userDAO) {
        System.out.println("\n--- RIGBUILD REGISTRATION ---");
        System.out.print("Enter Username: ");
        String username = scanner.nextLine();
        System.out.print("Enter Password: ");
        String password = scanner.nextLine();

        System.out.println("Select RigBuild Role:");
        System.out.println("1. Admin");
        System.out.println("2. Sales");
        System.out.println("3. Technician");
        System.out.print("Enter choice (1-3): ");

        int roleChoice = 1;
        if (scanner.hasNextInt()) {
            roleChoice = scanner.nextInt();
        }
        scanner.nextLine();

        String role;
        switch (roleChoice) {
            case 2: role = "Sales"; break;
            case 3: role = "Technician"; break;
            default: role = "Admin"; break;
        }

        boolean success = userDAO.registerUser(username, password, role);
        if (success) {
            System.out.println("\nAccount registered! Status is set to PENDING approval.");
        } else {
            System.out.println("\nRegistration failed. Username may already exist.");
        }
        pressEnterToContinue(scanner);
    }

    public static void showAdminMenu(Scanner scanner, UserDAO userDAO) {
        int choice;
        do {
            System.out.println("\n--- ADMIN PANEL ---");
            System.out.println("1. Approve Pending User Accounts");
            System.out.println("2. Manage Inventory");
            System.out.println("3. View Audit Logs");
            System.out.println("4. Logout");
            System.out.print("Select an option (1-4): ");

            while (!scanner.hasNextInt()) {
                scanner.next();
                System.out.print("Invalid input. Select an option (1-4): ");
            }
            choice = scanner.nextInt();
            scanner.nextLine();

            switch (choice) {
                case 1:
                    handleApproveUsers(scanner, userDAO);
                    break;
                case 2:
                    System.out.println("\n[Feature] Inventory management module.");
                    pressEnterToContinue(scanner);
                    break;
                case 3:
                    System.out.println("\n[Feature] Audit logs module.");
                    pressEnterToContinue(scanner);
                    break;
                case 4:
                    System.out.println("\nLogging out from Admin panel...");
                    break;
                default:
                    System.out.println("\nInvalid choice. Please select between 1 and 4.");
            }
        } while (choice != 4);
    }

    public static void handleApproveUsers(Scanner scanner, UserDAO userDAO) {
        System.out.println("\n--- PENDING USER ACCOUNTS ---");
        List<User> pendingUsers = userDAO.getPendingUsers();

        if (pendingUsers.isEmpty()) {
            System.out.println("No pending user registrations found.");
            pressEnterToContinue(scanner);
            return;
        }

        System.out.println(String.format("%-20s | %-15s", "Username", "Role"));
        System.out.println("-----------------------------------");
        for (User u : pendingUsers) {
            System.out.println(String.format("%-20s | %-15s", u.getUsername(), u.getRole()));
        }

        System.out.print("\nEnter Username to APPROVE (or type 'cancel' to go back): ");
        String targetUsername = scanner.nextLine().trim();

        if (!targetUsername.equalsIgnoreCase("cancel") && !targetUsername.isEmpty()) {
            boolean success = userDAO.approveUserByUsername(targetUsername);
            if (success) {
                System.out.println("\nUser account successfully APPROVED!");
            } else {
                System.out.println("\nFailed to approve user. Check the username.");
            }
        }
        pressEnterToContinue(scanner);
    }

    public static void showSalesMenu(Scanner scanner) {
        int choice;
        do {
            System.out.println("\n--- SALES MENU ---");
            System.out.println("1. Create Build Order");
            System.out.println("2. Logout");
            System.out.print("Select an option: ");
            while (!scanner.hasNextInt()) { scanner.next(); }
            choice = scanner.nextInt();
            scanner.nextLine();
        } while (choice != 2);
    }

    public static void showTechnicianMenu(Scanner scanner) {
        int choice;
        do {
            System.out.println("\n--- TECHNICIAN MENU ---");
            System.out.println("1. View Assigned Builds");
            System.out.println("2. Logout");
            System.out.print("Select an option: ");
            while (!scanner.hasNextInt()) { scanner.next(); }
            choice = scanner.nextInt();
            scanner.nextLine();
        } while (choice != 2);
    }

    public static void pressEnterToContinue(Scanner scanner) {
        System.out.print("\nPress Enter to continue...");
        scanner.nextLine();
    }
}